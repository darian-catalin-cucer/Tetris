package cucerdariancatalin.tetris.game

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

fun log(s: String) = println("${getDate()}: $s")
private fun getDate(): String = SimpleDateFormat("kk:mm:ss.SS").format(Date())

inline fun <T> profile(name: String, block: () -> T): T {
    var start = System.currentTimeMillis()
    val result = block()
    Log.i("profile", "$name ---> ${System.currentTimeMillis() - start}ms")
    return result
}