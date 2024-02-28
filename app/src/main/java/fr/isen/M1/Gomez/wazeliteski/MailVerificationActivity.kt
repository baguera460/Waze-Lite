package fr.isen.m1.gomez.wazeliteski

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import fr.isen.m1.gomez.wazeliteski.ui.theme.WazeLiteSkiTheme

interface MailVerificationInterface {
    fun sendVerificationEmail()
    fun signIn()
}

class MailVerificationActivity : ComponentActivity(), MailVerificationInterface {
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        val user = auth.currentUser

        if (user != null) {
            this.user = user
            setContent {
                WazeLiteSkiTheme {
                    Surface(
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MailVerificationView(this, user.email!!)
                    }
                }
            }
        } else {
            Toast.makeText(this, "No user found", Toast.LENGTH_SHORT).show()
            Log.e("MailVerificationActivity", "No user found")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun sendVerificationEmail() {
        user.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Verification email sent", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to send verification email", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    override fun signIn() {
        auth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MailVerificationView(activity: MailVerificationInterface, email: String) {
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
        Text(
            "In order to proceed into the app, you need to verify your email address ($email)",
            textAlign = TextAlign.Center,
            fontSize = 30.sp
        )
        Button(onClick = { activity.sendVerificationEmail() }) {
            Text("Send verification email")
        }
        Button(onClick = { activity.signIn() }) {
            Text("Sign In")
        }
    }
}