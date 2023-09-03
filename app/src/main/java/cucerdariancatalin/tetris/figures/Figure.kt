package cucerdariancatalin.tetris.figures

import cucerdariancatalin.tetris.game.colors

// Represents a 2D point with x and y coordinates
data class Point(val x: Int, val y: Int) {
    // Overloaded plus operator for adding two points
    operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)
}

// Interface representing a Tetris figure
interface Figure {
    var position: Point        // Current position of the figure
    var matrix: BitMatrix     // Matrix representing the shape of the figure
    var color: Int            // Color of the figure
    val points: Sequence<Point> // Sequence of points occupied by the figure
    var ghost: Figure?        // Ghost figure used for visualization

    // Create a clone of the figure
    fun clone(): Figure = javaClass.newInstance().also {
        it.matrix = matrix
        it.position = position
        it.color = color
        it.matrix = matrix.clone()
    }

    // Rotate the figure
    fun rotate() {
        matrix.rotate()
    }
}

// Abstract base class for Tetris figures
abstract class BaseFigure(override var color: Int = colors.random().toInt()) : Figure {
    override var position: Point = Point(0, 0)
    override var ghost: Figure? = null

    // Sequence of points occupied by the figure
    override val points: Sequence<Point> = sequence {
        for (r in matrix.array.indices) {
            val row = matrix.array[r]
            for (c in row.indices) {
                val item = row[c]
                if (item)
                    yield(Point(c + position.x, r + position.y))
            }
        }
    }

    // Custom toString() method for debugging purposes
    override fun toString(): String = "${javaClass.simpleName}\n$matrix"
}
