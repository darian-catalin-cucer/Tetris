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

@Suppress("LeakingThis")
abstract class BaseSurfaceView(context: Context, attrs: AttributeSet? = null) : SurfaceView(context, attrs) {
    private val holderCallback: SurfaceHolder.Callback = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder?) {
            surfaceReady = true
        }

        override fun surfaceDestroyed(holder: SurfaceHolder?) {
            surfaceReady = false
        }

        override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {}
    }

    protected var surfaceReady = false

    init {
        setZOrderOnTop(true)
        holder.setFormat(PixelFormat.TRANSPARENT)
        holder.addCallback(holderCallback)
    }

    @ObsoleteCoroutinesApi
    private val surfaceActor = MainScope().actor<Unit>(newSingleThreadContext(javaClass.simpleName), 2) {
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

    @ObsoleteCoroutinesApi
    override fun invalidate() {
        super.invalidate()
        if (surfaceReady) surfaceActor.offer(Unit)
    }

    @ObsoleteCoroutinesApi
    override fun postInvalidate() {
        super.postInvalidate()
        if (surfaceReady) surfaceActor.offer(Unit)
    }

    @ObsoleteCoroutinesApi
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        log("onDetachedFromWindow")
        surfaceActor.close()
        holder.removeCallback(holderCallback)
    }
}