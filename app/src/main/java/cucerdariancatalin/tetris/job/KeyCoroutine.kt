package cucerdariancatalin.tetris.job

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

// Default delay between key events
private const val DEFAULT_DELAY: Long = 40

// Default start delay for the first key event
private const val DEFAULT_START_DELAY: Long = 100

/**
 * A coroutine job that emits key events at regular intervals.
 *
 * @param contextProvider Function that provides the CoroutineContext for the coroutine job.
 * @param delay Delay between key events in milliseconds (default is [DEFAULT_DELAY]).
 * @param startDelay Delay before the first key event is emitted in milliseconds (default is [DEFAULT_START_DELAY]).
 * @param callback Callback function to be executed when a key event is emitted.
 */
class KeyCoroutine(
    private val contextProvider: () -> CoroutineContext,
    private val delay: Long = DEFAULT_DELAY,
    private val startDelay: Long = DEFAULT_START_DELAY,
    private val callback: () -> Unit
) : CoroutineJob() {

    private var firstCall: Boolean = true

    /**
     * Called when the coroutine job starts.
     */
    override fun onStart() {
        firstCall = true
    }

    /**
     * Provides the coroutine job that emits key events.
     *
     * @return The Job representing the coroutine.
     */
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
