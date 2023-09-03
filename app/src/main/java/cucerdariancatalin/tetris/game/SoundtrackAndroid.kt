package cucerdariancatalin.tetris.game

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import cucerdariancatalin.tetris.R

/**
 * Android implementation of the Soundtrack interface for playing game sounds.
 *
 * @param context The Android application context.
 */
class SoundtrackAndroid(context: Context) : Soundtrack {
    // Create a SoundPool with a maximum of 10 simultaneous streams.
    private var pool = SoundPool(10, AudioManager.STREAM_MUSIC, 0)

    // Load different sound variants for each action.
    private val move1 = pool.load(context, R.raw.move1, 1)
    private val move2 = pool.load(context, R.raw.move2, 1)
    private val move3 = pool.load(context, R.raw.move3, 1)
    private val move4 = pool.load(context, R.raw.move4, 1)
    private val wipe1 = pool.load(context, R.raw.wipe1, 1)
    private val wipe2 = pool.load(context, R.raw.wipe2, 1)
    private val wipe3 = pool.load(context, R.raw.wipe3, 1)
    private val wipe4 = pool.load(context, R.raw.wipe4, 1)
    private val start = pool.load(context, R.raw.start, 1)
    private val gameOver = pool.load(context, R.raw.game_over, 1)
    private val rotate = pool.load(context, R.raw.rotate, 1)
    private val levelUp = pool.load(context, R.raw.level_up, 1)

    /**
     * Play a specific sound with an optional variant.
     *
     * @param sound The type of sound to play.
     * @param variant The optional variant of the sound.
     */
    override fun play(sound: Sound, variant: Int) {
        val clip = when (sound) {
            Sound.MOVE -> when (variant) {
                1 -> move1
                2 -> move2
                3 -> move3
                else -> move4
            }
            Sound.WIPE -> when (variant) {
                1 -> wipe1
                2 -> wipe2
                3 -> wipe3
                else -> wipe4
            }
            Sound.ROTATE -> rotate
            Sound.START -> start
            Sound.GAME_OVER -> gameOver
            Sound.LEVEL_UP -> levelUp
            else -> kotlin.error("no sound")
        }
        // Play the selected sound with full volume and no looping.
        pool.play(clip, 1f, 1f, 1, 0, 1f)
    }

    /**
     * Release resources associated with the SoundtrackAndroid instance.
     */
    fun release() {
        pool.release()
    }
}