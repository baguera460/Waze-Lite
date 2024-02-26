package fr.isen.M1.Gomez.wazeliteski

import com.google.firebase.Firebase
import com.google.firebase.database.database

class GetDBData {
    companion object {
        val database = Firebase.database("https://waze-lite-ski-default-rtdb.europe-west1.firebasedatabase.app")
    }
}