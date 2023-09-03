package cucerdariancatalin.tetris.game

/**
 * Constants defining the dimensions of the game area.
 */
const val AREA_WIDTH = 10
const val AREA_HEIGHT = 20

/**
 * A constant representing a large positive value that can be used for certain calculations.
 */
const val INFINITY = Long.MAX_VALUE / 2

/**
 * An array of colors used for rendering the Tetris game blocks.
 * Each color is represented as a long integer in ARGB format.
 */
val colors = longArrayOf(
    0xFFA80000,   // Red
    0xFFCC44CC,   // Purple
    0xFF00CC55,   // Green
    0xFF2844E0,   // Blue
    0xFFEEEE77,   // Yellow
    0xFFDD8855,   // Orange
    0xFF804C40,   // Brown
    0xFFFFA890,   // Peach
    0xFF989498,   // Gray
    0xFFAAFF66,   // Light Green
    0xFF60B0FF    // Light Blue
)
