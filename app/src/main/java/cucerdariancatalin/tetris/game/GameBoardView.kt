package cucerdariancatalin.tetris.game

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import kotlinx.coroutines.*
import kotlin.math.roundToLong
import kotlin.system.measureNanoTime

/**
 * Custom view representing the game board.
 *
 * @param context The Android application context.
 * @param attrs AttributeSet, a collection of attributes from the XML layout.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GameBoardView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : BaseSurfaceView(context, attrs) {

    // Paint objects for drawing various elements
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }
    private val transparentPaint = Paint().apply { alpha = 127 }

    private var canvas: Canvas? = null
    private var bitmap: Bitmap? = null

    // Coroutine dispatcher for animations
    @OptIn(DelicateCoroutinesApi::class)
    @ObsoleteCoroutinesApi
    private var animationDispatcher = newSingleThreadContext("animation")

    private val brickSize get() = (width / AREA_WIDTH).toFloat()

    /**
     * Draw a block with a specified paint style.
     *
     * @param x The x-coordinate of the block.
     * @param y The y-coordinate of the block.
     * @param paint The paint style to use.
     * @param gap The gap between blocks.
     * @param radius The corner radius for rounded blocks.
     */
    private fun drawBlockWithPaint(x: Int, y: Int, paint: Paint, gap: Int = 2, radius: Float = 8f) {
        val left: Float = x * brickSize + gap
        val top: Float = y * brickSize + gap
        val right: Float = left + brickSize - gap * 2
        val bottom: Float = top + brickSize - gap * 2

        canvas?.drawRoundRect(
            left, top, right, bottom,
            radius, radius, paint
        )
    }

    /**
     * Fill a block at a specified position with a specified color.
     *
     * @param x The x-coordinate of the block.
     * @param y The y-coordinate of the block.
     * @param color The color to fill the block.
     */
    fun fillBlockAt(x: Int, y: Int, color: Int) {
        fillPaint.color = color
        drawBlockWithPaint(x, y, fillPaint)
    }

    /**
     * Stroke a block at a specified position with a specified color.
     *
     * @param x The x-coordinate of the block.
     * @param y The y-coordinate of the block.
     * @param color The color to stroke the block.
     */
    fun strokeBlockAt(x: Int, y: Int, color: Int) {
        strokePaint.color = color
        drawBlockWithPaint(x, y, strokePaint)
    }

    /**
     * Clear a block at a specified position.
     *
     * @param x The x-coordinate of the block to clear.
     * @param y The y-coordinate of the block to clear.
     */
    fun clearBlockAt(x: Int, y: Int) {
        drawBlockWithPaint(x, y, clearPaint, 0, 0f)
    }

    /**
     * Clear the entire game area.
     */
    fun clearArea() {
        canvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
    }

    /**
     * Coroutine function to animate the wiping of lines.
     *
     * @param lines The list of line indices to wipe.
     * @param first The index of the first line to wipe.
     */
    @OptIn(ObsoleteCoroutinesApi::class)
    suspend fun wipeLines(lines: List<Int>, first: Int) {
        // Fade nicely
        val brick = brickSize
        val iterations = 5
        repeat(iterations) {
            val paint = if (it == iterations - 1) clearPaint else transparentPaint
            for (line in lines) {
                canvas?.drawRect(0f, line * brick, width.toFloat(), (line + 1) * brick, paint)
            }
            invalidate()
            delay(50)
        }

        // Animate nicely
        var globalOffset = 0
        val animationJobs = mutableListOf<Job>()
        for (i in lines.size - 1 downTo 0) {
            globalOffset ++
            val distance = globalOffset * brick
            val startline = if (i == 0) {
                first - globalOffset
            } else {
                lines[i - 1] + 1
            }

            val size = lines[i] - startline

            if (size > 0) {
                animationJobs += MainScope().launch(animationDispatcher) {
                    val h: Float = size * brick
                    println("area start = $startline size=$size with distance $distance")
                    var y: Float = startline * brick
                    val slice = bitmap?.let { Bitmap.createBitmap(it, 0, y.toInt(), width, h.toInt()) }
                    val times = 9
                    for (j in 0..times) {
                        profile("draw canvas") {
                            val nanos = measureNanoTime {
                                canvas?.drawRect(0f, y, width.toFloat(), y + h, clearPaint)
                                y = startline * brick + j * distance / times
                                if (slice != null) {
                                    canvas?.drawBitmap(slice, 0f, y, fillPaint)
                                }
                                postInvalidate()
                            }
                            delay((16 - (nanos / 1000000.0).roundToLong()).coerceAtLeast(0))
                        }
                    }
                }
            }
        }
        animationJobs.forEach { it.join() }
    }

    /**
     * Override the onSizeChanged function to create a new bitmap and canvas when the size changes.
     *
     * @param w The new width of the view.
     * @param h The new height of the view.
     * @param oldw The old width of the view.
     * @param oldh The old height of the view.
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap!!)
    }

    /**
     * Override the onDraw function to draw the bitmap on the canvas.
     *
     * @param canvas The canvas to draw on.
     */
    override fun onDraw(canvas: Canvas) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap!!, 0f, 0f, fillPaint)
        }
    }

    /**
     * Override the onMeasure function to calculate the measured width and height of the view.
     *
     * @param widthMeasureSpec The width measurement specification.
     * @param heightMeasureSpec The height measurement specification.
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val originalHeight = MeasureSpec.getSize(heightMeasureSpec)
        val calculatedWidth = originalHeight / 2

        super.onMeasure(
            MeasureSpec.makeMeasureSpec(calculatedWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(originalHeight, MeasureSpec.EXACTLY)
        )
    }
}
