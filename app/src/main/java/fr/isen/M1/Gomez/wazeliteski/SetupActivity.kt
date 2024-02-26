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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth

interface OtherInterface {
    fun createSignInIntent()
}

class SetupActivity : ComponentActivity(), OtherInterface {
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        this.onSignInResult(res)
    }

    override fun createSignInIntent() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
            // ...
        } else {
            if (response == null) {
                Log.d("Login", "User cancelled sign in")
            } else {
                Log.d("Login", "Error: ${response.error?.errorCode}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val type =
            (intent.getSerializableExtra(SETUP_EXTRA_KEY) as? SetupType) ?: SetupType.LOGIN

        setContent {
            SetupView(type, this)
        }
        Log.d("lifeCycle", "Menu Activity - OnCreate")
    }

    companion object {
        const val SETUP_EXTRA_KEY = "SETUP_EXTRA_KEY"
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
                // update the UI
            } else {
                // If sign in fails, display a message to the user.
                Log.w("Login", "signInWithEmail:failure", task.exception)
                // update the UI
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupView(type: SetupType, activity: OtherInterface) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

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
        Text(text = "Sign in", textAlign = TextAlign.Center, fontSize = 50.sp)
        Divider(thickness = 20.dp, color = Color.White)
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true
        )
        Divider(thickness = 10.dp, color = Color.White)
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true
        )
        if (type == SetupType.REGISTER) {
            Divider(thickness = 10.dp, color = Color.White)
            TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                singleLine = true
            )
            Divider(thickness = 10.dp, color = Color.White)
            TextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                singleLine = true
            )
        }
        Divider(thickness = 10.dp, color = Color.White)
        Button(onClick = { loginAttempt(email, password) }) {
            if (type == SetupType.LOGIN) {
                Text(text = "Sign in")
            } else {
                Text(text = "Register")
            }
        }
        Button(onClick = { activity.createSignInIntent() }) {
            Text(text = "Other options")
        }
    }
}