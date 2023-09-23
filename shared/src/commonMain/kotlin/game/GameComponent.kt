package game

import androidx.compose.ui.geometry.Offset
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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import network.DAGRepository
import network.Resource
import network.Room
import network.RoomUser
import sockets.SocketEvent
import sockets.SocketEvent.StartGame
import sockets.SocketEventsListener
import sockets.SocketManager
import sockets.SocketManager.KEY_START_GAME
import kotlin.coroutines.CoroutineContext

interface GameComponent {

    val uiState: StateFlow<GameState>

    fun onIntent(intent: GameIntent)
}

class DefaultGameComponent(
    componentContext: ComponentContext,
    coroutineContext: CoroutineContext,
) : GameComponent, ComponentContext by componentContext, SocketEventsListener {

    private var _uiState = MutableStateFlow(GameState())

    override val uiState: StateFlow<GameState>
        get() = _uiState.asStateFlow()

    private val repository: DAGRepository = DAGRepository()

    override fun onIntent(intent: GameIntent) {
        handleIntent(intent)
    }

    private val scope = CoroutineScope(coroutineContext)
    private var polygonPoints: MutableList<MutableList<Offset>> = mutableListOf()
    private var userTurn: RoomUser = RoomUser()
    private var currentRound = 1
    private var totalRounds = 1
    private var roomInfo = Room()
    private var words: List<String> = emptyList()
    private lateinit var countDownTimer: CountDownTimer

    //Component is originator
    private var drawingState: CanvasPolygon = CanvasPolygon()
    private val canvasCommand = CanvasCommand()

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
            is OnDragMoved -> polygonPoints.last().add(intent.offset)
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

            OnDragStarted -> polygonPoints.add(mutableListOf())
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
            GameStart -> _uiState.update { it.copy(roundState = Drawing) }
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
            // inform server
            _uiState.update { it.copy(word = GameWord(word.actual, guessed)) }
            println("WON!!!!")
        } else if (isWrongGuess(word.actual, guessed)) {
            _uiState.update { it.copy(word = word.copy(guessed = guessed, wiggle = true)) }
            println("OOPSS!!!")
        }
    }

    private fun isCorrectGuess(actual: String, guessed: String) =
        actual.length == guessed.length && actual.lowercase() == guessed.lowercase()

    private fun isWrongGuess(actual: String, guessed: String) =
        actual.length == guessed.length && actual.lowercase() != guessed.lowercase()

    private fun saveSnapshot() {
        val (stroke, color) = _uiState.value.drawingInfo
        val newState = CanvasPolygon(polygonPoints.last(), stroke, color)
        canvasCommand.save(newState)
    }

    private fun restoreSnapshot() {
        canvasCommand.undo()?.let { cState ->
            this.drawingState = cState.polygons.lastOrNull() ?: CanvasPolygon()
            polygonPoints = cState.polygons.map { it.offsets }.toMutableList()
            _uiState.update { gameState ->
                gameState.copy(
                    drawingInfo = DrawingInfo(drawingState.strokeWidth, drawingState.paintColor),
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
        polygonPoints.clear()
        _uiState.update { it.copy(forceRestoreState = true, polygons = emptyList()) }
    }

    private fun getRoomInfo() {
        scope.launch {
            val userResponse = repository.getUser()
            check(userResponse is Resource.Success)
            val user = userResponse.data

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

    override fun onConnected() {
        TODO("Not yet implemented")
    }

    override fun onDisconnected() {
        TODO("Not yet implemented")
    }

    override fun onFailure(reason: String) {
        TODO("Not yet implemented")
    }

    override fun onEvent(event: SocketEvent) {
        scope.launch(Dispatchers.Main) {
            when (event) {
                StartGame -> {
                    _uiState.update { it.copy(roundState = Starting) }
                }
                else -> {}
            }
        }
    }

    companion object {
        private const val CHOOSE_DISPLAY_SECS = 3
    }
}