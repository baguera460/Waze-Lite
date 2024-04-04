package fr.isen.m1.gomez.wazeliteski.data

import java.io.Serializable

data class OpinionLift(
    var id: Int = 0,
    val comment: String = "",
    val lift: String = "",
    val user: String = "",
) : Serializable