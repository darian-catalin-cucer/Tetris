package cucerdariancatalin.tetris.game

import cucerdariancatalin.tetris.figures.*
import cucerdariancatalin.tetris.job.KeyCoroutine
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.selects.select
import kotlin.coroutines.CoroutineContext

// Constants
private const val BASE_DELAY = 800L

// Array of available figures
private val figures = arrayOf(
    IFigure::class.java,
    IFigure::class.java,
    LFigure::class.java,
    LFlippedFigure::class.java,
    SFigure::class.java,
    SFlippedFigure::class.java,
    SquareFigure::class.java,
    TFigure::class.java
)

/**
 * The main game class responsible for game logic and interaction with the view.
 *
 * @param view The game view responsible for rendering and user input.
 * @param soundtrack The soundtrack to play game sounds.
 * @param uiCoroutineContext The coroutine context for UI interactions.
 */
@Suppress("DEPRECATION")
@OptIn(ObsoleteCoroutinesApi::class)
class Game(
    private val view: GameView,
    private val soundtrack: Soundtrack,
    private val uiCoroutineContext: CoroutineContext
) {
    var soundEnabled = true

    private val board: Board = Board(view)
    private val score: Score = Score {
        view.score = score
        if (view.level < level && view.level != 0 && soundEnabled)
            soundtrack.play(Sound.LEVEL_UP)
        view.level = level
    }
    private lateinit var nextFigure: Figure

    private var isStarted: Boolean = false
    var isPaused: Boolean = false

    private var stopper: Job = Job()

    private val uiContextProvider = {
        uiCoroutineContext + stopper
    }

    private var downKeyCoroutine: KeyCoroutine = KeyCoroutine(uiContextProvider, 30) {
        score.awardSpeedUp()
        fallActor.trySend(Unit).isSuccess
    }

    private var leftKeyCoroutine: KeyCoroutine = KeyCoroutine(uiContextProvider) {
        board.moveFigure(Direction.LEFT.movement)
    }

    private var rightKeyCoroutine: KeyCoroutine = KeyCoroutine(uiContextProvider) {
        board.moveFigure(Direction.RIGHT.movement)
    }

    private val fallActor = CoroutineScope(uiContextProvider()).actor<Unit>(uiContextProvider()) {
        log("actor started")
        while (isActive) {
            var falling = true
            nextFigure = randomFigure(false)
            drawPreview()
            board.drawFigure()
            while (falling) {
                falling = select {
                    onReceive {
                        board.moveFigure(Direction.DOWN.movement)
                    }
                    calculateDelay()
                    run {
                        board.moveFigure(Direction.DOWN.movement)
                    }
                }
            }
            downKeyCoroutine.stop()
            if (gameOver()) {
                isStarted = false
                if (soundEnabled) soundtrack.play(Sound.GAME_OVER)
                coroutineContext.cancel()
            } else {
                board.fixFigure()

                val lines = board.getFilledLinesIndices()
                if (lines.isNotEmpty()) {
                    if (soundEnabled) soundtrack.play(Sound.WIPE, lines.size)
                    board.wipeLines(lines)
                    view.wipeLines(lines)
                    score.awardLinesWipe(lines.size)
                }
                nextFigure.position = board.startingPosition(nextFigure)
                board.currentFigure = nextFigure
            }
        }
        view.gameOver()
        log("actor stopped")
    }

    /**
     * Start the game.
     */
    fun start() {
        if (isStarted) error("Can't start twice")
        isStarted = true

        view.clearArea()

        score.awardStart()

        board.currentFigure = randomFigure()
        fallActor.trySend(Unit).isSuccess

        if (soundEnabled) soundtrack.play(Sound.START)
    }

    /**
     * Stop the game.
     */
    fun stop() {
        stopper.cancel()
    }

    /**
     * Pause or resume the game.
     */
    fun pause() {
        MainScope().isActive || return
        if (soundEnabled) soundtrack.play(Sound.ROTATE)
        isPaused = !isPaused
        if (!isPaused)
            fallActor.trySend(Unit).isSuccess
    }

    private fun drawPreview() {
        view.clearPreviewArea()
        nextFigure.points.forEach {
            view.drawPreviewBlockAt(it.x, it.y, nextFigure.color)
        }
        view.invalidate()
    }

    private fun calculateDelay(): Long = if (!isPaused) {
        (BASE_DELAY - score.level * 50).coerceAtLeast(1)
    } else {
        INFINITY
    }

    private fun gameOver(): Boolean = board.currentFigure.position.y <= 0

    private fun randomFigure(bringToStart: Boolean = true): Figure {
        val cls = figures.random()
        val figure = cls.newInstance()
        if (bringToStart)
            figure.position = board.startingPosition(figure)
        return figure
    }

    /**
     * Get the index of the top brick line on the game board.
     */
    fun getTopBrickLine() = board.area.array.indexOfFirst { it -> !it.all { !it } }

    /**
     * Handle the left key press event.
     */
    fun onLeftPressed() {
        if (isStarted) leftKeyCoroutine.start()
        playMoveSound()
    }

    /**
     * Handle the right key press event.
     */
    fun onRightPressed() {
        if (isStarted) rightKeyCoroutine.start()
        playMoveSound()
    }

    /**
     * Handle the down key press event.
     */
    fun onDownPressed() {
        if (isStarted) downKeyCoroutine.start()
        playMoveSound()
    }

    /**
     * Handle the up key press event (rotate action).
     */
    fun onUpPressed() {
        if (isStarted) board.rotateFigure()
        if (soundEnabled) soundtrack.play(Sound.ROTATE)
    }

    /**
     * Handle the down key release event.
     */
    fun onDownReleased() = downKeyCoroutine.stop()

    /**
     * Handle the left key release event.
     */
    fun onLeftReleased() = leftKeyCoroutine.stop()

    /**
     * Handle the right key release event.
     */
    fun onRightReleased() = rightKeyCoroutine.stop()

    private fun playMoveSound() {
        if (soundEnabled) soundtrack.play(Sound.MOVE, (Math.random() * 4 + 1).toInt())
    }
}
