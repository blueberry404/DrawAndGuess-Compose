package ui.game

import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.lifecycle.subscribe
import core.CountDownTimer
import core.GlobalData
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
import models.Player
import models.Room
import models.RoomUser
import models.User
import network.DAGRepository
import network.Resource
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import sockets.SocketEvent
import sockets.SocketEvent.NextRound
import sockets.SocketEvent.NextTurn
import sockets.SocketEvent.StartGame
import sockets.SocketEvent.SyncDrawing
import sockets.SocketEvent.TurnOver
import sockets.SocketEventsListener
import sockets.SocketManager
import sockets.SocketManager.KEY_CORRECT_GUESS
import sockets.SocketManager.KEY_END_GAME
import sockets.SocketManager.KEY_END_ROUND
import sockets.SocketManager.KEY_END_TURN
import sockets.SocketManager.KEY_NEXT_ROUND
import sockets.SocketManager.KEY_NEXT_TURN
import sockets.SocketManager.KEY_START_GAME
import sockets.SocketManager.KEY_WRONG_GUESS
import ui.game.models.CanvasPolygon
import ui.game.models.CanvasState
import ui.game.models.DrawingInfo
import ui.game.models.GameIntent
import ui.game.models.GameIntent.ClearCanvas
import ui.game.models.GameIntent.Erase
import ui.game.models.GameIntent.GameStart
import ui.game.models.GameIntent.OnDragEnded
import ui.game.models.GameIntent.OnDragMoved
import ui.game.models.GameIntent.OnDragStarted
import ui.game.models.GameIntent.SelectColor
import ui.game.models.GameIntent.SelectLetter
import ui.game.models.GameIntent.SelectStrokeWidth
import ui.game.models.GameIntent.StateRestoreCompleted
import ui.game.models.GameIntent.Undo
import ui.game.models.GameIntent.WiggleAnimationCompleted
import ui.game.models.GameState
import ui.game.models.GameWord
import ui.game.models.TurnState
import ui.game.models.TurnState.Choosing
import ui.game.models.TurnState.Drawing
import ui.game.models.TurnState.Starting
import ui.game.undo.CanvasCommand
import kotlin.coroutines.CoroutineContext

interface GameComponent {

    val uiState: StateFlow<GameState>

    fun onIntent(intent: GameIntent)
}

class DefaultGameComponent(
    componentContext: ComponentContext,
    coroutineContext: CoroutineContext,
    private val navigateToEnd: () -> Unit,
) : GameComponent, ComponentContext by componentContext, SocketEventsListener, KoinComponent {

    private var _uiState = MutableStateFlow(GameState())

    override val uiState: StateFlow<GameState>
        get() = _uiState.asStateFlow()

    private val repository: DAGRepository by inject()

    private val backCallback = BackCallback {
        Napier.d { "Back button disabled" }
    }

    override fun onIntent(intent: GameIntent) {
        handleIntent(intent)
    }

    private val scope = CoroutineScope(coroutineContext)
    private var userTurn: RoomUser = RoomUser()
    private var currentRound = 1
    private var currentTurn = 1
    private var totalRounds = 1
    private var roomInfo = Room()
    private var words: List<List<String>> = emptyList()
    private lateinit var countDownTimer: CountDownTimer
    private var syncJob: Job? = null
    private lateinit var user: User

    //Component is originator
    private val canvasCommand = CanvasCommand()
    private var drawingState: CanvasState = CanvasState(mutableListOf())

    init {
        subscribeLifecycle()
        backHandler.register(backCallback)
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

    private fun getRoomInfo() {
        scope.launch {
            val userResponse = repository.getUser()
            check(userResponse is Resource.Success)
            user = userResponse.data

            val response = repository.getRoom(GlobalData.room.id)
            if (response is Resource.Success) {
                roomInfo = response.data
                userTurn = roomInfo.users[currentTurn - 1]
                totalRounds = roomInfo.gameRounds
                words = roomInfo.words
                val currentWord =
                    if (words.isNotEmpty()) words[0][0] else throw IllegalStateException("There should be some word")
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
                        turnState = Choosing,
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
                _uiState.update { it.copy(turnState = Drawing) }
                startJobForCanvasSync()
            }
            GameIntent.TimeOver -> {
                _uiState.update {
                    it.copy(
                        turnState = TurnState.TimeOver,
                        gameOverMessage = "Time's up!!"
                    )
                }
                syncJob?.cancel()
                if (roomInfo.isAdmin) {
                    if (currentTurn == roomInfo.users.size) {
                        SocketManager.signalForGame(KEY_END_TURN)
                    } else if (currentRound == totalRounds) {
                        SocketManager.signalForGame(KEY_END_ROUND)
                    }
                }
            }
        }
    }


    //////////////////////////// GUESSED WORD ////////////////////////////

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
                    turnState = TurnState.Ended,
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

    //////////////////////////// CANVAS ////////////////////////////

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


    //////////////////////////// MANAGE ROUNDS ////////////////////////////

    private fun startTimerForChooseWordDisplay() {
        countDownTimer = CountDownTimer(CHOOSE_DISPLAY_SECS) {
            if (it == CHOOSE_DISPLAY_SECS) {
                SocketManager.signalForGame(KEY_START_GAME)
            }
        }.also { it.start() }
    }

    private fun startTimerForTurnOverDisplay() {
        syncJob?.cancel()

        if (wasLastRound() || roomInfo.isAdmin) {
            countDownTimer = CountDownTimer(ROUND_OVER_DISPLAY_SECS) {
                if (it == ROUND_OVER_DISPLAY_SECS) {
                    checkForNextTurn()
                }
            }.also { it.start() }
        }
    }

    private fun checkForNextTurn() {
        if (wasLastRound()) {
            SocketManager.signalForGame(KEY_END_GAME)
            SocketManager.disconnect()
            navigateToEnd()

        } else if (currentTurn == roomInfo.users.size) {
            SocketManager.signalForGame(KEY_NEXT_ROUND)
        } else {
            SocketManager.signalForGame(KEY_NEXT_TURN)
        }
    }

    private fun wasLastRound() = currentTurn == roomInfo.users.size &&
        currentRound == roomInfo.gameRounds

    private fun updateUserTurn() {
        userTurn = roomInfo.users[currentTurn - 1]
        val currentWord = words[currentRound - 1][currentTurn - 1]
        val isCurrentUser = user.id == userTurn.id

        _uiState.update {
            it.copy(
                turnState = Choosing,
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

    //////////////////////////// SOCKETS ////////////////////////////

    override fun onConnected() {

    }

    override fun onDisconnected() {

    }

    override fun onFailure(reason: String) {
        Napier.e { reason }
    }

    override fun onEvent(event: SocketEvent) {
        scope.launch(Dispatchers.Main) {
            when (event) {
                StartGame -> {
                    _uiState.update { it.copy(turnState = Starting) }
                }
                is SyncDrawing -> {
                    drawingState = event.canvasState
                    _uiState.update { gameState ->
                        gameState.copy(polygons = event.canvasState.polygons)
                    }
                }
                is TurnOver -> {
                    if (event.isWinner != null) {
                        val name: String? = if (event.winnerId == user.id)
                            "You"
                        else {
                            roomInfo.users.find { it.id == event.winnerId.orEmpty() }?.username
                        }
                        if (name != null) {
                            _uiState.update {
                                it.copy(
                                    turnState = TurnState.Ended,
                                    gameOverMessage = "$name guessed the word!"
                                )
                            }
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                turnState = TurnState.TimeOver,
                                gameOverMessage = "Time's up!!"
                            )
                        }
                    }
                    startTimerForTurnOverDisplay()
                }
                NextTurn -> {
                    currentTurn++
                    updateUserTurn()
                }
                NextRound -> {
                    countDownTimer.cancel()
                    currentRound++
                    currentTurn = 1
                    updateUserTurn()
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