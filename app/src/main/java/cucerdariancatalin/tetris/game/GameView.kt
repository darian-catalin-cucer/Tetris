package cucerdariancatalin.tetris.game

/**
 * Interface defining the view contract for the Tetris game.
 */
interface GameView {
    /**
     * The current score of the game.
     */
    var score: Int

    /**
     * The current level of the game.
     */
    var level: Int

    /**
     * Draw a colored block at the specified position on the game board.
     *
     * @param x The x-coordinate of the block.
     * @param y The y-coordinate of the block.
     * @param color The color to fill the block with.
     * @param style The paint style for the block (STOKE or FILL).
     */
    fun drawBlockAt(x: Int, y: Int, color: Int, style: PaintStyle)

    /**
     * Clear the block at the specified position on the game board.
     *
     * @param x The x-coordinate of the block.
     * @param y The y-coordinate of the block.
     */
    fun clearBlockAt(x: Int, y: Int)

    /**
     * Notify the game view that the game is over.
     */
    fun gameOver()

    /**
     * Clear the entire game board area.
     */
    fun clearArea()

    /**
     * Wipe the specified lines on the game board.
     *
     * @param lines The list of line indices to wipe.
     */
    suspend fun wipeLines(lines: List<Int>)

    /**
     * Draw a colored preview block at the specified position.
     *
     * @param x The x-coordinate of the preview block.
     * @param y The y-coordinate of the preview block.
     * @param color The color to fill the preview block with.
     */
    fun drawPreviewBlockAt(x: Int, y: Int, color: Int)

    /**
     * Clear the preview area where the next figure is displayed.
     */
    fun clearPreviewArea()

    /**
     * Optional API. Used in Android for better performance.
     */
    fun invalidate()
}