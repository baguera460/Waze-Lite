package fr.isen.m1.gomez.wazeliteski

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fr.isen.m1.gomez.wazeliteski.ui.theme.WazeLiteSkiTheme

enum class SetupType {
    LOGIN, REGISTER;
}

interface SetupInterface {
    fun setupChosen(optionChosen: SetupType)
}

class MainActivity : ComponentActivity(), SetupInterface {
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Firebase.database.reference

        setContent {
            WazeLiteSkiTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainView(this)
                }
            }
        }
    }

    override fun setupChosen(optionChosen: SetupType) {
        val intent = Intent(this, SetupActivity::class.java)
        intent.putExtra(SetupActivity.SETUP_EXTRA_KEY, optionChosen)
        startActivity(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(activity: SetupInterface) {
    TopAppBar(title = {
        Text(
            text = "WazeLiteSki",
            fontSize = 40.sp,
            textAlign = TextAlign.Center
        )
    }, colors = TopAppBarDefaults.topAppBarColors(Color(0xFF6200EE)))
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome to WazeLiteSki", textAlign = TextAlign.Center, fontSize = 25.sp)
        Divider(thickness = 40.dp, color = Color.White)
        Button(onClick = { activity.setupChosen(SetupType.LOGIN) }) {
            Text(text = "Sign in")
        }
        Divider(thickness = 10.dp, color = Color.White)
        Button(onClick = { activity.setupChosen(SetupType.REGISTER) }) {
            Text(text = "Sign up")
        }
    }
}