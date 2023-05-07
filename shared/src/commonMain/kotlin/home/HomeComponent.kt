package home

import com.arkivanov.decompose.ComponentContext

interface HomeComponent {
    fun onGameOptionSelected(gameMode: GameMode)
}

class DefaultHomeComponent(
    componentContext: ComponentContext,
    private val onOptionSelected: (GameMode) -> Unit,
): HomeComponent {

    override fun onGameOptionSelected(gameMode: GameMode) {
        onOptionSelected(gameMode)
    }
}