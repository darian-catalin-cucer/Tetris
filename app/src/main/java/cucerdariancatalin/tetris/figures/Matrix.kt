package cucerdariancatalin.tetris.figures

import java.util.*

// Interface representing a generic matrix
interface Matrix<T> {
    val array: Array<Array<T>>
    val width: Int get() = array[0].size
    val height: Int get() = array.size

    /**
     * Rotates the matrix.
     */
    fun rotate()

    operator fun get(row: Int, column: Int): T = array[row][column]
    operator fun set(row: Int, column: Int, value: T) {
        array[row][column] = value
    }

    operator fun get(p: Point): T = this[p.y, p.x]
    operator fun set(p: Point, value: T) {
        this[p.y, p.x] = value
    }
}

// Class representing a matrix of boolean values (BitMatrix)
class BitMatrix(override val array: Array<Array<Boolean>>) : Matrix<Boolean> {
    override fun rotate() {
        val copy = copyArray()

        for (r in array.indices) {
            val row = array[r]
            for (c in row.indices) {
                array[r][c] = copy[row.size - c - 1][r]
            }
        }
    }

    private fun copyArray() =
        Arrays.copyOf(array.map { Arrays.copyOf(it, it.size) }.toTypedArray(), array.size)

    override fun toString(): String {
        return array.joinToString("") {
            it.map { if (it) '■' else '·' }.joinToString("") + "\n"
        }
    }

    companion object {
        // Create a BitMatrix using a builder lambda
        operator fun invoke(builder: MatrixBuilder.() -> Unit): BitMatrix {
            val context = MatrixBuilder()
            context.builder()
            return BitMatrix(context.getArray())
        }

        // Create a BitMatrix with the specified dimensions and a function to initialize its values
        operator fun invoke(width: Int, height: Int, f: (x: Int, y: Int) -> Boolean): BitMatrix {
            val array = Array(width) { x -> Array(height) { y -> f(x, y) } }
            return BitMatrix(array)
        }
    }

    // Clone the BitMatrix
    fun clone(): BitMatrix = BitMatrix(copyArray())
}

// Builder class for creating a BitMatrix from a textual representation
class MatrixBuilder {
    private val rows: MutableList<String> = mutableListOf()

    operator fun String.unaryPlus() {
        rows += this
    }

    fun getArray() = rows
        .map {
            it.map { it == '1' }.toTypedArray()
        }.toTypedArray()
}
