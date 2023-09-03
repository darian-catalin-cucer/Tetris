package cucerdariancatalin.tetris.game

import cucerdariancatalin.tetris.figures.BitMatrix
import cucerdariancatalin.tetris.figures.Figure
import cucerdariancatalin.tetris.figures.Point

// Class representing the game board
class Board(private val view: GameView) {

    // The game area represented by a BitMatrix
    var area: BitMatrix = BitMatrix(AREA_HEIGHT, AREA_WIDTH) { _, _ -> false }

    // The currently active figure on the board
    lateinit var currentFigure: Figure

    // Draws the current figure on the game board
    fun drawFigure() = with(currentFigure) {
        if (ghost == null) {
            // Find the ghost figure
            for (i in 0 until AREA_HEIGHT - position.y) {
                val movement = Point(0, i)
                if (!canMove(movement, currentFigure)) {
                    ghost = clone()
                    ghost!!.position = Point(position.x, position.y + i - 1)
                    break
                }
            }
        }

        // Draw the ghost figure's points
        ghost?.points?.forEach {
            view.drawBlockAt(it.x, it.y, currentFigure.color, PaintStyle.STOKE)
        }

        // Draw the current figure's points
        points.forEach {
            view.drawBlockAt(it.x, it.y, currentFigure.color, PaintStyle.FILL)
        }
        view.invalidate()
    }

    /**
     * Clear the figure in both the preview and game board areas.
     */
    private fun clearFigure(figure: Figure) {
        figure
            .points
            .forEach {
                /*view.clearBlockAt(it.x, it.y)*/
            }
        figure
            .ghost
            ?.points
            ?.forEach {
                /*view.clearBlockAt(it.x, it.y)*/
            }
    }

    /**
     * Move the specified figure by the given movement.
     * @return true if the movement was successful
     */
    fun moveFigure(movement: Point, figure: Figure = currentFigure): Boolean {
        // Check if the figure can be moved in the specified direction
        canMove(movement, figure) || return false
        clearFigure(figure)

        // Invalidate the ghost figure if there is horizontal movement
        if (movement.x != 0)
            figure.ghost = null

        // Update the figure's position and draw it
        figure.position += movement
        drawFigure()
        return true
    }

    /**
     * Check if the figure can be moved in a direction specified by the movement.
     */
    private fun canMove(movement: Point, figure: Figure): Boolean = figure
        .points
        .all {
            val nextPoint = it + movement
            !nextPoint.outOfArea() && !area[nextPoint]
        }

    // Check if a point is out of the game area
    private fun Point.outOfArea(): Boolean = x >= AREA_WIDTH || x < 0 || y >= AREA_HEIGHT || y < 0

    /**
     * Rotate the current figure.
     */
    fun rotateFigure() {
        val newFigure = currentFigure.clone().apply {
            rotate()

            // Handle edge cases to keep the figure within the game area
            while (!points.all { it.x >= 0 }) {
                position += Direction.RIGHT.movement
            }
            while (!points.all { it.x < AREA_WIDTH }) {
                position += Direction.LEFT.movement
            }
            while (!points.all { it.y < AREA_HEIGHT }) {
                position += Direction.UP.movement
            }

            // Try to fix unexpected collisions
            if (!points.all { !area[it] }) {
                if (canMove(Direction.RIGHT.movement, this))
                    position += Direction.RIGHT.movement
                else if (canMove(Direction.LEFT.movement, this))
                    position += Direction.LEFT.movement
            }
        }

        // Check if the rotated figure can be placed on the board
        if (newFigure.points.all { !area[it] }) {
            clearFigure(currentFigure)
            currentFigure = newFigure
            currentFigure.ghost = null
            drawFigure()
        }
    }

    /**
     * Fix the current figure when it reaches the bottom of the board.
     */
    fun fixFigure() {
        for (point in currentFigure.points) {
            area[point] = true
        }
    }

    /**
     * Get the indices of filled lines in the board.
     */
    fun getFilledLinesIndices(): List<Int> = area.array
        .mapIndexed { i, row -> i to row }
        .filter { it.second.all { it } }
        .map { it.first }

    /**
     * Wipe filled lines on the board.
     */
    fun wipeLines(lines: List<Int>) {
        val cropped = area.array.filter { !it.all { it } }.toMutableList()
        repeat(lines.size) {
            cropped.add(0, Array(AREA_WIDTH) { false })
        }
        area = BitMatrix(cropped.toTypedArray())
    }

    // Calculate the starting position for a figure
    fun startingPosition(figure: Figure) = Point((AREA_WIDTH - figure.matrix.width) / 2, 0)
}
