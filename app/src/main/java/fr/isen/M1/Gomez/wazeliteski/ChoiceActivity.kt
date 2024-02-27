package fr.isen.m1.gomez.wazeliteski

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import fr.isen.m1.gomez.wazeliteski.ui.theme.WazeLiteSkiTheme

interface ChoiceInterface {
    fun choiceChosen()
}

class ChoiceActivity: ComponentActivity(), ChoiceInterface {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WazeLiteSkiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChoiceView(this)
                }
            }
        }
    }

    override fun choiceChosen() {
        Firebase.auth.signOut()
        finish()
    }
}

@Composable
fun ChoiceView(activity: ChoiceInterface) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("You are signed in")
        Button(onClick = { activity.choiceChosen() }) {
            Text("Sign out")
        }
    }
}