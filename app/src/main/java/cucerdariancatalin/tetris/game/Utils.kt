package cucerdariancatalin.tetris.game

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility function for logging messages with timestamps.
 *
 * @param s The message to be logged.
 */
fun log(s: String) = println("${getDate()}: $s")

/**
 * Get the current timestamp in the "kk:mm:ss.SS" format.
 *
 * @return The formatted timestamp.
 */
private fun getDate(): String = SimpleDateFormat("kk:mm:ss.SS").format(Date())

/**
 * A profiling function for measuring the execution time of a code block.
 *
 * @param name The name or description of the code block being profiled.
 * @param block The code block to be executed and profiled.
 * @return The result of the code block.
 */
inline fun <T> profile(name: String, block: () -> T): T {
    val start = System.currentTimeMillis()
    val result = block()
    val executionTime = System.currentTimeMillis() - start
    Log.i("profile", "$name ---> $executionTime ms")
    return result
}