package fr.isen.m1.gomez.wazeliteski

import android.os.Bundle
import androidx.activity.ComponentActivity
import fr.isen.m1.gomez.wazeliteski.data.Slope

class SlopeLinkActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val slope_name = intent.getSerializableExtra(SLOPE_EXTRA_KEY) as? String ?: "Barrigart"
    }

    companion object {
        val SLOPE_EXTRA_KEY = "SLOPE_EXTRA_KEY"
    }
}