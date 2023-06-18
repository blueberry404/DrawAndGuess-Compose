package core.extension

import androidx.compose.ui.Modifier

fun Modifier.ifOnly(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier =
    if (condition)
        then(modifier(Modifier))
    else
        this