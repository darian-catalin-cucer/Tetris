package cucerdariancatalin.tetris.data

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameState(var score: Int = 0, var level: Int = 0) : Parcelable {

    // Parcelable Implementation for GameState
    companion object : Parceler<GameState> {

        // Write object values to a Parcel
        override fun GameState.write(parcel: Parcel, flags: Int) {
            parcel.writeInt(score)
            parcel.writeInt(level)
        }

        // Create a GameState object from a Parcel
        override fun create(parcel: Parcel): GameState {
            return GameState(parcel)
        }
    }

    // Constructor for creating a GameState object from a Parcel
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt()
    )
}
