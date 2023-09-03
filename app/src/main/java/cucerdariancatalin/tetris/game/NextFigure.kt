package cucerdariancatalin.tetris.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import cucerdariancatalin.tetris.figures.Point

/**
 * Custom view to display the next figure that will appear in the game.
 *
 * @param context The application context.
 * @param attrs Optional attribute set.
 */
class NextFigure @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : BaseSurfaceView(context, attrs) {
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val bricks = mutableListOf<Point>()
    private val brickSize get() = (width / 4).toFloat()
    private val radius = 8f
    private val gap = 2

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

    /**
     * Fill a block in the next figure display with the specified color at the given coordinates.
     *
     * @param x The x-coordinate of the block.
     * @param y The y-coordinate of the block.
     * @param color The color to fill the block with.
     */
    fun fillBlockAt(x: Int, y: Int, color: Int) {
        paint.color = color
        bricks += Point(x, y)
    }

    /**
     * Clear all blocks from the next figure display.
     */
    fun clear() {
        bricks.clear()
    }

    override fun onDraw(canvas: Canvas) {
        for (b in bricks) {
            val left: Float = b.x * brickSize + gap
            val top: Float = b.y * brickSize + gap
            val right: Float = left + brickSize - gap * 2
            val bottom: Float = top + brickSize - gap * 2

            canvas.drawRoundRect(
                left, top, right, bottom,
                radius, radius, paint
            )
        }
    }
}