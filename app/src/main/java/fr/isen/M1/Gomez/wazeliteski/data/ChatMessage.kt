package fr.isen.m1.gomez.wazeliteski.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ChatMessage(
    @SerializedName("message") val message: String? = null,
    @SerializedName("uid") val uid: String? = null,
) : Serializable
