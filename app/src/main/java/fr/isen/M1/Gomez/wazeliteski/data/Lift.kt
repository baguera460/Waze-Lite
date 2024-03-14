package fr.isen.m1.gomez.wazeliteski.data

import fr.isen.m1.gomez.wazeliteski.R
import java.io.Serializable

data class Lift(
    val name: String = "",
    val type: String = "",
    var state: Boolean = true,
    var slopes: List<String> = listOf(),
    var index: Int = 0
) : Serializable