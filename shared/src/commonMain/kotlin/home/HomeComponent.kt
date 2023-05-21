package home

interface HomeComponent {
    fun onGameOptionSelected(gameMode: GameMode)
}

class DefaultHomeComponent(
    private val onOptionSelected: (GameMode) -> Unit,
): HomeComponent {

    override fun onGameOptionSelected(gameMode: GameMode) {
        onOptionSelected(gameMode)
    }
}