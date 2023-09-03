package cucerdariancatalin.tetris.game

import cucerdariancatalin.tetris.figures.Point

/**
 * Enum class representing the possible movement directions in the Tetris game.
 *
 * @property movement The Point representing the direction of movement.
 */
enum class Direction(val movement: Point) {
    /**
     * Up direction.
     */
    UP(Point(0, -1)),

    /**
     * Down direction.
     */
    DOWN(Point(0, 1)),

    /**
     * Left direction.
     */
    LEFT(Point(-1, 0)),

    /**
     * Right direction.
     */
    RIGHT(Point(1, 0))
}
