package fr.isen.m1.gomez.wazeliteski.database;

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DataBaseHelper {
    companion object {
        val database = Firebase.database("https://waze-lite-ski-default-rtdb.europe-west1.firebasedatabase.app/")
    }
}