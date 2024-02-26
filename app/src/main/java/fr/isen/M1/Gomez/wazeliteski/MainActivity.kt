package fr.isen.M1.Gomez.wazeliteski

import android.os.Bundle
import android.util.Log
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fr.isen.M1.Gomez.wazeliteski.ui.theme.WazeLiteSkiTheme


class MainActivity : ComponentActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WazeLiteSkiTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //Greeting("Android")
                    LoginView(this)
                }
            }
        }
    }
}

@Composable
fun GetData() {
    val database = Firebase.database.reference
    val postListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            Log.d("Database_", snapshot.toString())
            if (snapshot.exists()) {
                Log.d("Database: ", "Data exists")
                snapshot.children.forEach {
                    Log.d("Database: ", it.toString())
                }
            } else {
                Log.d("Database: ", "No data")
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("Database", error.toString())
        }
    }
}

fun loginAttempt(email: String, password: String) {
    val auth = FirebaseAuth.getInstance()
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener() { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("Login", "signInWithEmail:success")
                val user = auth.currentUser
                // updateUI(user)
            } else {
                // If sign in fails, display a message to the user.
                Log.w("Login", "signInWithEmail:failure", task.exception)
                // updateUI(null)
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginView(activity: MainActivity, modifier: Modifier = Modifier) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    TopAppBar(title = { Text(text = "WazeLiteSki") })
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Sign in", modifier = modifier, textAlign = TextAlign.Center, fontSize = 50.sp)
        Divider(thickness = 20.dp, color = Color.White)
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, singleLine = true)
        Divider(thickness = 10.dp, color = Color.White)
        TextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, singleLine = true)
        Divider(thickness = 10.dp, color = Color.White)
        Button(onClick = { loginAttempt(email, password) }) {
            Text(text = "Login")
        }
    }
}