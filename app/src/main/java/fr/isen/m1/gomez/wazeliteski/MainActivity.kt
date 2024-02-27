package fr.isen.m1.gomez.wazeliteski

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.isen.m1.gomez.wazeliteski.ui.theme.WazeLiteSkiTheme

class MainActivity : ComponentActivity() {/*
    val database = FirebaseDatabase.getInstance("https://waze-lite-ski-default-rtdb.europe-west1.firebasedatabase.app/")
    var myRef = database.getReference("Slope")

    var pisteData by remember { mutableStateOf("aucune donnée") }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WazeLiteSkiTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Greeting()
                    val context = LocalContext.current
                    val intent = Intent(context, SlopeActivity::class.java)
                    context.startActivity(intent)
                }
            }

        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    Column {
        Text(text = "Hello")
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Test")
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WazeLiteSkiTheme {
        Greeting()

    }
}
