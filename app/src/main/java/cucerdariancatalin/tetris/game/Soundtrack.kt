package cucerdariancatalin.tetris.game

/**
 * Enumeration of different game sounds.
 */
enum class Sound {
    START,        // Sound played at the beginning of the game.
    GAME_OVER,    // Sound played when the game is over.
    PAUSE,        // Sound played when the game is paused.
    MOVE,         // Sound played when a game piece is moved.
    ROTATE,       // Sound played when a game piece is rotated.
    WIPE,         // Sound played when lines are wiped.
    LEVEL_UP      // Sound played when the player levels up.
}

/**
 * Interface for defining a game's soundtrack.
 */
interface Soundtrack {
    /**
     * Play a specific game sound with an optional variant.
     *
     * @param sound The type of sound to play.
     * @param variant The optional variant of the sound.
     */
    fun play(sound: Sound, variant: Int = 0)
}
