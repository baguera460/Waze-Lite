package fr.isen.m1.gomez.wazeliteski.data

import androidx.compose.ui.graphics.Color
import java.io.Serializable

data class Slope(
    val name: String = "",
    val color: Int = 0,
    var state: Boolean = true,
    var index: Int = 0,
    var end: Boolean = false,
    var next: List<Int>? = null
) : Serializable

enum class Level {
    Green, Blue, Red, Black;

    fun colorId(): Color {
        return when(this) {
            Green -> Color(20, 148, 20)
            Blue -> Color(21, 96, 189)
            Red -> Color(187, 11, 11)
            Black -> Color.Black
        }
    }

    companion object {
        fun from(color: Int): Level {
            return when(color) {
                1 -> Green
                2 -> Blue
                3 -> Red
                4 -> Black
                else -> Green
            }
        }
    }
}