package cucerdariancatalin.tetris.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.isActive
import kotlinx.coroutines.newSingleThreadContext

// Abstract base class for SurfaceView-based views
@Suppress("LeakingThis")
abstract class BaseSurfaceView(context: Context, attrs: AttributeSet? = null) : SurfaceView(context, attrs) {
    // Callback to handle SurfaceHolder events
    private val holderCallback: SurfaceHolder.Callback = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            surfaceReady = true
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            surfaceReady = false
        }

        override fun surfaceChanged(holder: SurfaceHolder, p1: Int, p2: Int, p3: Int) {}
    }

    // Flag to indicate if the surface is ready
    protected var surfaceReady = false

    init {
        setZOrderOnTop(true)
        holder.setFormat(PixelFormat.TRANSPARENT)
        holder.addCallback(holderCallback)
    }

    // Coroutine-based actor for rendering on the surface
    @ObsoleteCoroutinesApi
    private val surfaceActor = MainScope().actor<Unit>(
        newSingleThreadContext(javaClass.simpleName), 2
    ) {
        while (isActive) {
            receive()
            var canvas: Canvas? = null
            try {
                canvas = holder.lockCanvas(null)
                if (canvas != null) {
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                    draw(canvas)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas)
                }
            }
        }
    }

    // Invalidate the view and send a rendering request to the actor
    @ObsoleteCoroutinesApi
    override fun invalidate() {
        super.invalidate()
        if (surfaceReady) surfaceActor.trySend(Unit).isSuccess
    }

    // Post-invalidate the view and send a rendering request to the actor
    @ObsoleteCoroutinesApi
    override fun postInvalidate() {
        super.postInvalidate()
        if (surfaceReady) surfaceActor.trySend(Unit).isSuccess
    }

    // Clean up resources when the view is detached from the window
    @ObsoleteCoroutinesApi
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        log("onDetachedFromWindow")
        surfaceActor.close()
        holder.removeCallback(holderCallback)
    }
}