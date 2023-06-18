package game

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import core.Colors
import game.GameIntent.OnDragMoved
import game.GameIntent.OnDragStarted
import game.GameIntent.SelectColor
import game.GameIntent.SelectLetter
import game.GameIntent.SelectStrokeWidth
import game.GameIntent.WiggleAnimationCompleted
import home.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

class GameViewModel(
    coroutineContext: CoroutineContext,
): InstanceKeeper.Instance {

    private var _uiState = MutableStateFlow(GameState())
    val uiState: StateFlow<GameState> = _uiState

    private val scope = CoroutineScope(coroutineContext)
    private var flowTimer: Flow<Int>? = null

    private val colors = listOf(
        Color(Colors.AVATAR_1),
        Color(Colors.AVATAR_2),
        Color(Colors.AVATAR_3),
        Color(Colors.AVATAR_4),
        Color(Colors.AVATAR_5),
        Color(Colors.AVATAR_6),
        Color(Colors.AVATAR_7),
    )

    private val drawingInfo: MutableList<MutableList<Offset>> = mutableListOf()

    init {
        otherDrawing()
    }

    fun handleIntent(intent: GameIntent) {
        when (intent) {
            is SelectLetter -> checkGuessedWord(intent.letter)
            OnDragStarted -> drawingInfo.add(mutableListOf())
            is OnDragMoved -> drawingInfo.last().add(intent.offset)
            WiggleAnimationCompleted -> _uiState.update { it.copy(
                word = GameWord(actual = it.word.actual)
            ) }
            is SelectColor -> _uiState.update { it.copy(
                drawingInfo = it.drawingInfo.copy(paintColor = intent.color)
            ) }
            is SelectStrokeWidth -> _uiState.update { it.copy(
                drawingInfo = it.drawingInfo.copy(strokeWidth = intent.width)
            ) }
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
        }
        else if (isCorrectGuess(word.actual, guessed)) {
            // inform server
            _uiState.update { it.copy(word = GameWord(word.actual, guessed)) }
            println("WON!!!!")
        }
        else if(isWrongGuess(word.actual, guessed)) {
            _uiState.update { it.copy(word = word.copy(guessed = guessed, wiggle = true)) }
            println("OOPSS!!!")
        }
    }

    private fun isCorrectGuess(actual: String, guessed: String) =
        actual.length == guessed.length && actual.lowercase() == guessed.lowercase()

    private fun isWrongGuess(actual: String, guessed: String) =
        actual.length == guessed.length && actual.lowercase() != guessed.lowercase()


    fun otherDrawing() {
        _uiState.value = GameState(
            isCurrentUser = true,
            currentTurnUserId = "123",
            currentUsername = "Guest 388h",
            isDrawing = true,
            isChoosing = false,
            players = listOf(
                Player("123", "Guest123", 12, true, true, getColor()),
                Player("456", "Guest234", 0, false, false, getColor()),
                Player("789", "Guest345", 0, false, false, getColor()),
            ),
        word = GameWord("Water")
        )
    }

    fun OtherChoosing() {
        _uiState.value = GameState(
            isCurrentUser = false,
            currentTurnUserId = "123",
            currentUsername = "Guest 388h",
            isDrawing = false,
            isChoosing = true,
            players = listOf(
                Player("123", "Guest123", 12, false, false, getColor()),
                Player("456", "Guest234", 0, true, false, getColor()),
                Player("789", "Guest345", 0, false, true, getColor()),
            ))
    }

    override fun onDestroy() {
        scope.cancel()
    }

    private fun getColor() = colors[Random.nextInt(colors.size)]

    private fun initTimer() {
        flowTimer = (1..20)
            .asSequence()
            .asFlow()
            .cancellable()
            .onEach { delay(1_000) }
            .onCompletion {

            }

        scope.launch {
            flowTimer?.collectLatest { sec ->
                _uiState.update { it.copy(currentTime = sec) }
            }
        }
    }
}