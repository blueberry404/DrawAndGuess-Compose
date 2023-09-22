package core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CountDownTimer(private val durationInSeconds: Int, private val callback: (Int) -> Unit) {

    private var job: Job? = null
    private var scope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    fun start() {
        job = scope.launch {
            repeat(durationInSeconds) {
                callback(it)
                delay(1_000)
            }
            callback(durationInSeconds)
        }
    }

    fun cancel() {
        job?.cancel()
    }
}