package cucerdariancatalin.tetris

import android.annotation.SuppressLint
import android.app.GameState
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import cucerdariancatalin.tetris.databinding.ActivityMainBinding
import cucerdariancatalin.tetris.game.Game
import cucerdariancatalin.tetris.game.GameView
import cucerdariancatalin.tetris.game.PaintStyle
import cucerdariancatalin.tetris.game.SoundtrackAndroid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Suppress("DEPRECATION")
@OptIn(ObsoleteCoroutinesApi::class)
@Parcelize
class MainActivity : AppCompatActivity(), GameView, Parcelable {
    private var game: Game? = null
    private lateinit var soundtrack: SoundtrackAndroid
    private lateinit var binding: ActivityMainBinding
    private var gameState = GameState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        soundtrack = SoundtrackAndroid(this)
        initListeners(binding)
    }

    override fun onDestroy() {
        super.onDestroy()
        game?.stop()
        soundtrack.release()
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    private fun initListeners(binding: ActivityMainBinding) {
        binding.startButton.setOnClickListener {
            game?.stop()
            game = Game(this, soundtrack, Dispatchers.Main)
            game?.soundEnabled = binding.soundCheck.isChecked
            game?.start()

            binding.startButton.text = "Restart"
            binding.pauseButton.text = "Pause"
        }

        binding.soundCheck.setOnCheckedChangeListener { _, isChecked ->
            game?.soundEnabled = isChecked
        }

        binding.pauseButton.setOnClickListener {
            game?.pause()
            if (game?.isPaused == true) {
                binding.pauseButton.text = "Resume"
            } else {
                binding.pauseButton.text = "Pause"
            }
        }

        binding.leftButton.setOnTouchListener(ButtonTouchListener({ game?.onLeftPressed() }, { game?.onLeftReleased() }))
        binding.rightButton.setOnTouchListener(ButtonTouchListener({ game?.onRightPressed() }, { game?.onRightReleased() }))
        binding.upButton.setOnTouchListener(ButtonTouchListener({ game?.onUpPressed() }, { }))
        binding.downButton.setOnTouchListener(ButtonTouchListener({ game?.onDownPressed() }, { game?.onDownReleased() }))
    }

    override var score: Int
        get() = gameState.score
        @SuppressLint("SetTextI18n")
        set(value) {
            gameState.score = value
            binding.scoreLabel.text = "Score: $value"
        }

    override var level: Int
        get() = gameState.level
        @SuppressLint("SetTextI18n")
        set(value) {
            value.also { gameState.level = it }
            binding.levelLabel.text = "Level: $value"
        }

    override fun drawBlockAt(x: Int, y: Int, color: Int, style: PaintStyle) {
        when (style) {
            PaintStyle.FILL -> binding.boardView.fillBlockAt(x, y, color)
            PaintStyle.STOKE -> binding.boardView.strokeBlockAt(x, y, color)
        }
    }

    override fun clearBlockAt(x: Int, y: Int) {
        binding.boardView.clearBlockAt(x, y)
    }

    override fun gameOver() {
        AlertDialog.Builder(this)
            .setMessage("Game Over")
            .setPositiveButton(android.R.string.ok) { d, _ -> d.dismiss() }
            .setCancelable(false)
            .show()
    }

    override fun clearArea() {
        binding.boardView.clearArea()
    }

    override suspend fun wipeLines(lines: List<Int>) {
        binding.boardView.wipeLines(lines, game?.getTopBrickLine() ?: 0)
    }

    override fun drawPreviewBlockAt(x: Int, y: Int, color: Int) {
        binding.nextFigure.fillBlockAt(x, y, color)
    }

    override fun clearPreviewArea() {
        binding.nextFigure.clear()
    }

    override fun invalidate() {
        binding.boardView.invalidate()
        binding.nextFigure.invalidate()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> game?.onLeftPressed()
            KeyEvent.KEYCODE_DPAD_RIGHT -> game?.onRightPressed()
            KeyEvent.KEYCODE_DPAD_UP -> game?.onUpPressed()
            KeyEvent.KEYCODE_DPAD_DOWN -> game?.onDownPressed()
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> game?.onLeftReleased()
            KeyEvent.KEYCODE_DPAD_RIGHT -> game?.onRightReleased()
            KeyEvent.KEYCODE_DPAD_DOWN -> game?.onDownReleased()
        }
        return super.onKeyUp(keyCode, event)
    }

    companion object : Parceler<MainActivity> {
        override fun MainActivity.write(parcel: Parcel, flags: Int) {
            parcel.writeInt(gameState.score)
            parcel.writeInt(gameState.level)
            parcel.writeParcelable(gameState, flags)
        }

        override fun create(parcel: Parcel): MainActivity {
            val mainActivity = MainActivity()
            mainActivity.gameState = parcel.readParcelable(GameState::class.java.classLoader) ?: GameState()
            return mainActivity
        }
    }
}

class ButtonTouchListener(private val pressed: () -> Unit, private val released: () -> Unit) : View.OnTouchListener {

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        when (p1?.action) {
            MotionEvent.ACTION_DOWN -> pressed()
            MotionEvent.ACTION_UP -> released()
        }
        return false
    }
}