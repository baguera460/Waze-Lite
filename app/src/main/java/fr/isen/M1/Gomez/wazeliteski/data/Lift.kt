package fr.isen.M1.Gomez.wazeliteski.data


data class Lift(
    val name: String ="",
    val type:String = "telesiege",
    var state : Boolean = true,

    )

enum class LiftType{
    TELESIEGE, TIRE_FESSE;
}