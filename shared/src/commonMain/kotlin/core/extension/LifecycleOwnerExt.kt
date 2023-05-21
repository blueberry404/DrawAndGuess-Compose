package core.extension

import com.arkivanov.essenty.lifecycle.LifecycleOwner
import core.CoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

fun LifecycleOwner.scopeCoroutine(context: CoroutineContext): CoroutineScope =
    CoroutineScope(context, lifecycle)