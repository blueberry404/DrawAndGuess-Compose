package game

import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.subscribe
import core.CountDownTimer
import core.GlobalData
import game.GameIntent.ClearCanvas
import game.GameIntent.Erase
import game.GameIntent.GameStart
import game.GameIntent.OnDragEnded
import game.GameIntent.OnDragMoved
import game.GameIntent.OnDragStarted
import game.GameIntent.SelectColor
import game.GameIntent.SelectLetter
import game.GameIntent.SelectStrokeWidth
import game.GameIntent.StateRestoreCompleted
import game.GameIntent.Undo
import game.GameIntent.WiggleAnimationCompleted
import game.RoundState.Choosing
import game.RoundState.Drawing
import game.RoundState.Starting
import game.undo.CanvasCommand
import home.Player
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import network.DAGRepository
import network.Resource
import network.Room
import network.RoomUser
import network.User
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import sockets.SocketEvent
import sockets.SocketEvent.NewRound
import sockets.SocketEvent.RoundOver
import sockets.SocketEvent.StartGame
import sockets.SocketEvent.SyncDrawing
import sockets.SocketEventsListener
import sockets.SocketManager
import sockets.SocketManager.KEY_CORRECT_GUESS
import sockets.SocketManager.KEY_END_GAME
import sockets.SocketManager.KEY_NEW_ROUND
import sockets.SocketManager.KEY_START_GAME
import sockets.SocketManager.KEY_WRONG_GUESS
import kotlin.coroutines.CoroutineContext

interface GameComponent {

    val uiState: StateFlow<GameState>

    fun onIntent(intent: GameIntent)
}

class DefaultGameComponent(
    componentContext: ComponentContext,
    coroutineContext: CoroutineContext,
) : GameComponent, ComponentContext by componentContext, SocketEventsListener, KoinComponent {

    private var _uiState = MutableStateFlow(GameState())

    override val uiState: StateFlow<GameState>
        get() = _uiState.asStateFlow()

    private val repository: DAGRepository by inject()

    override fun onIntent(intent: GameIntent) {
        handleIntent(intent)
    }

    private val scope = CoroutineScope(coroutineContext)
    private var userTurn: RoomUser = RoomUser()
    private var currentRound = 1
    private var totalRounds = 1
    private var roomInfo = Room()
    private var words: List<String> = emptyList()
    private lateinit var countDownTimer: CountDownTimer
    private var syncJob: Job? = null
    private lateinit var user: User

    //Component is originator
    private val canvasCommand = CanvasCommand()
    private var drawingState: CanvasState = CanvasState(mutableListOf())

    init {
        subscribeLifecycle()
        getRoomInfo()
    }

    private fun subscribeLifecycle() {
        lifecycle.subscribe(onPause = {
            SocketManager.removeListener()
        }, onResume = {
            SocketManager.setListener(this)
        }, onDestroy = {
            scope.cancel()
        })
    }

    private fun handleIntent(intent: GameIntent) {
        when (intent) {
            is SelectLetter -> checkGuessedWord(intent.letter)
            is OnDragMoved -> {
                drawingState.polygons.last().offsets.add(intent.offset)
            }
            is SelectColor -> _uiState.update {
                it.copy(
                    drawingInfo = it.drawingInfo.copy(paintColor = intent.color)
                )
            }

            is SelectStrokeWidth -> _uiState.update {
                it.copy(
                    drawingInfo = it.drawingInfo.copy(strokeWidth = intent.width)
                )
            }

            OnDragStarted -> {
                val (strokeWidth, color) = _uiState.value.drawingInfo
                drawingState.polygons.add(
                    CanvasPolygon(
                        strokeWidth = strokeWidth,
                        paintColor = color
                    )
                )
            }
            OnDragEnded -> saveSnapshot()
            WiggleAnimationCompleted -> _uiState.update {
                it.copy(
                    word = GameWord(actual = it.word.actual)
                )
            }

            Undo -> restoreSnapshot()
            ClearCanvas -> onClearCanvas()
            Erase -> {}
            StateRestoreCompleted -> _uiState.update { it.copy(forceRestoreState = false) }
            GameStart -> {
                _uiState.update { it.copy(roundState = Drawing) }
                startJobForCanvasSync()
            }
            GameIntent.TimeOver -> {
                _uiState.update {
                    it.copy(
                        roundState = RoundState.TimeOver,
                        gameOverMessage = "Time's up!!"
                    )
                }
                syncJob?.cancel()
                if (roomInfo.isAdmin) {
                    SocketManager.signalForGame(KEY_END_GAME)
                }
            }
        }
    }

    private fun checkGuessedWord(letter: Char) {
        val word = _uiState.value.word
        if (letter == '!' && word.guessed.isEmpty()) return
        if (letter == '!') { //backspace
            val guessed = word.guessed.substring(0, word.guessed.length - 1)
            _uiState.update { it.copy(word = GameWord(word.actual, guessed)) }
            return
        }
        val guessed = word.guessed + letter
        if (word.actual.length > guessed.length) {
            _uiState.update { it.copy(word = GameWord(word.actual, guessed)) }
        } else if (isCorrectGuess(word.actual, guessed)) {
            _uiState.update {
                it.copy(
                    word = GameWord(word.actual, guessed),
                    roundState = RoundState.Ended,
                    gameOverMessage = "You guessed the word!"
                )
            }
            SocketManager.signalForGame(KEY_CORRECT_GUESS)
        } else if (isWrongGuess(word.actual, guessed)) {
            _uiState.update { it.copy(word = word.copy(guessed = guessed, wiggle = true)) }
            SocketManager.signalForGame(KEY_WRONG_GUESS)
        }
    }

    private fun isCorrectGuess(actual: String, guessed: String) =
        actual.length == guessed.length && actual.lowercase() == guessed.lowercase()

    private fun isWrongGuess(actual: String, guessed: String) =
        actual.length == guessed.length && actual.lowercase() != guessed.lowercase()

    private fun saveSnapshot() {
        canvasCommand.save(drawingState.polygons.last())
    }

    private fun restoreSnapshot() {
        canvasCommand.undo()?.let { cState ->
            this.drawingState = cState
            val polygon = drawingState.polygons.last()

            _uiState.update { gameState ->
                gameState.copy(
                    drawingInfo = DrawingInfo(polygon.strokeWidth, polygon.paintColor),
                    forceRestoreState = true,
                    polygons = cState.polygons
                )
            }
        } ?: run {
            onClearCanvas()
        }
    }

    private fun onClearCanvas() {
        canvasCommand.clear()
        drawingState.polygons.last().offsets.clear()
        _uiState.update { it.copy(forceRestoreState = true, polygons = emptyList()) }
    }

    private fun getRoomInfo() {
        scope.launch {
            val userResponse = repository.getUser()
            check(userResponse is Resource.Success)
            user = userResponse.data

            val response = repository.getRoom(GlobalData.room.id)
            if (response is Resource.Success) {
                roomInfo = response.data
                userTurn = roomInfo.users[currentRound - 1]
                totalRounds = roomInfo.gameRounds
                words = roomInfo.words
                val currentWord =
                    if (words.isNotEmpty()) words[0] else throw IllegalStateException("There should be some word")
                val players = roomInfo.users.map {
                    val color =
                        Color(("ff" + it.avatarColor.removePrefix("#").lowercase()).toLong(16))
                    Player(
                        id = it.id,
                        name = it.username,
                        score = 0,
                        isDrawing = false,
                        isCurrentUser = false,
                        color = color
                    )
                }
                _uiState.update {
                    GameState(
                        players = players,
                        isCurrentUser = user.id == userTurn.id,
                        currentTurnUserId = userTurn.id,
                        currentUsername = userTurn.username,
                        totalRounds = roomInfo.gameRounds,
                        roundState = Choosing,
                        totalTimeInSec = 60,
                        currentTime = 0,
                        word = GameWord(actual = currentWord),
                    )
                }
              if (_uiState.value.isCurrentUserChoosing) {
                  startTimerForChooseWordDisplay()
              }
            } else {
                Napier.e { "An error occured" }
            }
        }
    }

    private fun startTimerForChooseWordDisplay() {
        countDownTimer = CountDownTimer(CHOOSE_DISPLAY_SECS) {
            if (it == CHOOSE_DISPLAY_SECS) {
                SocketManager.signalForGame(KEY_START_GAME)
            }
        }.also { it.start() }
    }

    private fun startTimerForRoundOverDisplay() {
        syncJob?.cancel()
        if (!roomInfo.isAdmin) return

        countDownTimer = CountDownTimer(ROUND_OVER_DISPLAY_SECS) {
            if (it == ROUND_OVER_DISPLAY_SECS) {
                if (currentRound + 1 > roomInfo.gameRounds) {
                    //Disconnect
                } else {
                    SocketManager.signalForGame(KEY_NEW_ROUND)
                }
            }
        }.also { it.start() }
    }

    private fun startJobForCanvasSync() {
        syncJob = scope.launch(Dispatchers.Default) {
            while (_uiState.value.isCurrentUserDrawing) {
                canvasCommand.peek()?.let {
                    SocketManager.syncCanvas(it)
                }
                delay(700)
            }
        }
    }

    override fun onConnected() {
        TODO("Not yet implemented")
    }

    override fun onDisconnected() {
        TODO("Not yet implemented")
    }

    override fun onFailure(reason: String) {
        Napier.e { reason }
    }

    override fun onEvent(event: SocketEvent) {
        scope.launch(Dispatchers.Main) {
            when (event) {
                StartGame -> {
                    _uiState.update { it.copy(roundState = Starting) }
                }
                is SyncDrawing -> {
                    drawingState = event.canvasState
                    _uiState.update { gameState ->
                        gameState.copy(polygons = event.canvasState.polygons)
                    }
                }
                is RoundOver -> {
                    if (event.isWinner != null) {
                        val name: String? = if (event.winnerId == user.id)
                            "You"
                        else {
                            roomInfo.users.find { it.id == event.winnerId.orEmpty() }?.username
                        }
                        if (name != null) {
                            _uiState.update {
                                it.copy(
                                    roundState = RoundState.Ended,
                                    gameOverMessage = "$name guessed the word!"
                                )
                            }
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                roundState = RoundState.TimeOver,
                                gameOverMessage = "Time's up!!"
                            )
                        }
                    }
                    startTimerForRoundOverDisplay()
                }
                NewRound -> {
                    currentRound++
                    userTurn = roomInfo.users[currentRound - 1]
                    val currentWord = words[currentRound - 1]
                    val isCurrentUser = user.id == userTurn.id

                    _uiState.update {
                        it.copy(
                            roundState = Choosing,
                            isCurrentUser = isCurrentUser,
                            currentTurnUserId = userTurn.id,
                            currentUsername = userTurn.username,
                            currentTime = 0,
                            word = GameWord(actual = currentWord),
                            gameOverMessage = "",
                        )
                    }
                    if (isCurrentUser) {
                        startTimerForChooseWordDisplay()
                    }
                }
                else -> {}
            }
        }
    }

    companion object {
        private const val CHOOSE_DISPLAY_SECS = 5
        private const val ROUND_OVER_DISPLAY_SECS = 5
    }
}