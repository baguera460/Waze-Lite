package fr.isen.m1.gomez.wazeliteski.data

import fr.isen.m1.gomez.wazeliteski.R

data class Lift(
    val name: String = "",
    val type: String = "",
    var state : Boolean = true,
    var slopes: List<String> = listOf(),
    var index: Int = 0
)
enum class LiftType{
    TELESIEGE, TIREFESSE;

    fun drawableId():Int{
        return when(this){
            TELESIEGE -> R.drawable.telesiege
            TIREFESSE -> R.drawable.fesse

        }
    }
    companion object{
        fun from(type: String):LiftType{
            return when(type){
                "télésiège"-> TELESIEGE
                "tire-fesse" -> TIREFESSE
                else -> TIREFESSE
                }
        }
    }
}
