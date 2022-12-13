package cucerdariancatalin.tetris.job

import kotlinx.coroutines.Job
import java.util.concurrent.CancellationException

@Suppress("MemberVisibilityCanPrivate")
abstract class CoroutineJob {

    private var job: Job? = null

    val isRunning get() = job?.isActive == true

    fun start() {
        if (isRunning) return
        onStart()
        job = provideJob()
    }

    fun stop() {
        job?.cancel(CancellationException("job ${javaClass.simpleName} stopped"))
        onStop()
    }

    open fun onStart() {}
    open fun onStop() {}
    protected abstract fun provideJob(): Job
}