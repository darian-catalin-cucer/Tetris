package cucerdariancatalin.tetris.job

import kotlinx.coroutines.Job
import java.util.concurrent.CancellationException

/**
 * An abstract class representing a coroutine job.
 */
@Suppress("MemberVisibilityCanPrivate")
abstract class CoroutineJob {

    private var job: Job? = null

    /**
     * Check if the coroutine job is currently running.
     */
    val isRunning get() = job?.isActive == true

    /**
     * Start the coroutine job.
     */
    fun start() {
        if (isRunning) return
        onStart()
        job = provideJob()
    }

    /**
     * Stop the coroutine job.
     */
    fun stop() {
        job?.cancel(CancellationException("job ${javaClass.simpleName} stopped"))
        onStop()
    }

    /**
     * Callback method called when the coroutine job starts.
     */
    open fun onStart() {}

    /**
     * Callback method called when the coroutine job stops.
     */
    open fun onStop() {}

    /**
     * Provides the coroutine job to be implemented by subclasses.
     *
     * @return The Job representing the coroutine.
     */
    protected abstract fun provideJob(): Job
}