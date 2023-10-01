package models

enum class GameMode {
    Single,
    Many,
    None;

    override fun toString() = when (this) {
        Single -> "Single"
        Many -> "Many"
        None -> ""
    }
}