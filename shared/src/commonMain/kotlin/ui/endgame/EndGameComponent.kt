package ui.endgame

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import io.github.aakira.napier.Napier

interface EndGameComponent {
    fun navigateToHome()
}

class DefaultEndGameComponent(
    componentContext: ComponentContext,
    private val popToHome: () -> Unit,
): EndGameComponent, ComponentContext by componentContext {

    private val backCallback = BackCallback {
        popToHome()
    }

    init {
        backHandler.register(backCallback)
    }

    override fun navigateToHome() {
        popToHome()
    }
}