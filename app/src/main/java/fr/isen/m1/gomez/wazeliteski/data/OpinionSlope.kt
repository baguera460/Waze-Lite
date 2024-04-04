package fr.isen.m1.gomez.wazeliteski.data

import java.io.Serializable

data class OpinionSlope(
    var id: Int = 0,
    val comment: String = "",
    val slope: String = "",
    val user: String = "",

) : Serializable
