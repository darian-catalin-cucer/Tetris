package cucerdariancatalin.tetris.game

/**
 * Class representing the game score and level, with the ability to award points
 * and level up based on game events.
 *
 * @param callback Callback function to invoke when the score or level changes.
 */
class Score(private val callback: Score.() -> Unit) {

    /**
     * Current score of the game.
     */
    var score: Int = 0
        set(value) {
            field = value
            if (field / level > 2000)
                awardLevelUp()
        }

    /**
     * Current level of the game.
     */
    var level: Int = 0

    /**
     * Award additional points and increase the speed based on the current level.
     */
    fun awardSpeedUp() {
        score += level
        callback()
    }

    /**
     * Award a level up and additional points when certain conditions are met.
     */
    private fun awardLevelUp() {
        level++
        score += 100
        callback()
    }

    /**
     * Award points based on the number of lines wiped.
     *
     * @param count Number of lines wiped simultaneously.
     */
    fun awardLinesWipe(count: Int) {
        score += when (count) {
            1 -> 100
            2 -> 250
            3 -> 500
            4 -> 1000
            else -> 0
        }
        callback()
    }

    /**
     * Award a level up and invoke the callback when the game starts.
     */
    fun awardStart() {
        level++
        callback()
    }
}