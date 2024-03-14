package fr.isen.m1.gomez.wazeliteski.data

import com.google.gson.annotations.SerializedName

data class User(
    val index: Int? = null,
    @SerializedName("mail") val email: String? = null,
    @SerializedName("phone") val phoneNumber: String? = null,
)
