package cucerdariancatalin.tetris.job

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

private const val DEFAULT_DELAY: Long = 40
private const val DEFAULT_START_DELAY: Long = 100

class KeyCoroutine(
    private val contextProvider: () -> CoroutineContext,
    private val delay: Long = DEFAULT_DELAY,
    private val startDelay: Long = DEFAULT_START_DELAY,
    private val callback: () -> Unit
) : CoroutineJob() {

    private var firstCall: Boolean = true

    override fun onStart() {
        firstCall = true
    }

    override fun provideJob(): Job = MainScope().launch(contextProvider()) {
        while (isActive) {
            callback()
            if (firstCall) {
                firstCall = false
                delay(startDelay)
            } else {
                delay(delay)
            }
        }
    }
}