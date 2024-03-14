package fr.isen.m1.gomez.wazeliteski.data

import java.io.Serializable

data class ChatRoom(
    val messages: List<ChatMessage>? = null,
) : Serializable
