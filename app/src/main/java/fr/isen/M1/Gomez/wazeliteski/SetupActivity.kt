package fr.isen.m1.gomez.wazeliteski

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import fr.isen.m1.gomez.wazeliteski.ui.theme.WazeLiteSkiTheme

interface OtherInterface {
    fun signInWithGoogle()
    fun loginAttempt(email: String, password: String)
    fun registerAttempt(
        email: String,
        password: String,
        confirmPassword: String,
        phoneNumber: String
    )

    fun back()
}

class SetupActivity : ComponentActivity(), OtherInterface {
    private val requestCode = 2

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, ChoiceActivity::class.java)
            startActivity(intent)
        } else {
            val type =
                (intent.getSerializableExtra(SETUP_EXTRA_KEY) as? SetupType) ?: SetupType.LOGIN

            setContent {
                WazeLiteSkiTheme {
                    Surface(
                        color = MaterialTheme.colorScheme.background
                    ) {
                        SetupView(type, this)
                    }
                }
            }
        }

        Log.d("lifeCycle", "Menu Activity - OnCreate")
    }

    override fun signInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        val mGoogleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, requestCode)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val intent = Intent(this, ChoiceActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    // display a message to the user
                }
            }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == this.requestCode) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d(TAG, "signInResult:success")
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(TAG, "signInResult:failed with status code ${e.statusCode}")
            }
        }
    }

    override fun loginAttempt(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Login", "signInWithEmail:success")
                    val intent = Intent(this, ChoiceActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.w("Login", "signInWithEmail:failure", task.exception)
                    // display a message to the user
                }
            }
    }

    override fun registerAttempt(
        email: String,
        password: String,
        confirmPassword: String,
        phoneNumber: String
    ) {
        if (password == confirmPassword) {
            val database = Firebase.database.reference
            val user = HashMap<String, String>(
                mapOf(
                    "mail" to email,
                    "password" to password,
                    "phone" to phoneNumber
                )
            )
            database.child("user").get().addOnSuccessListener {
                if (it.value != null) {
                    val users = it.value as HashMap<*, *>

                    users.filterValues { value -> value == email }.keys.firstOrNull()?.let {
                        Log.w("Register", "User already exists")
                        // display a message to the user
                    }

                    users.filterValues { value -> value == phoneNumber }.keys.firstOrNull()?.let {
                        Log.w("Register", "Phone number already exists")
                        // display a message to the user
                    }
                }
                database.child("user").push().key?.let {
                    database.child("user").child(it).setValue(
                        mapOf(
                            "mail" to email,
                            "password" to password,
                            "phone" to phoneNumber
                        )
                    )
                }
            }
        } else {
            Log.w("Register", "Passwords do not match")
            // display a message to the user
        }
    }

    override fun back() {
        finish()
    }

    companion object {
        const val SETUP_EXTRA_KEY = "SETUP_EXTRA_KEY"
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
        Button(onClick = { activity.back() }) {
            Text("Back")
        }
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
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                autoCorrect = false
            )
        )
        Divider(thickness = 10.dp, color = Color.White)
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                autoCorrect = false,
            ),
            visualTransformation = PasswordVisualTransformation(mask = '•')
        )
        if (type == SetupType.REGISTER) {
            Divider(thickness = 10.dp, color = Color.White)
            TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    autoCorrect = false,
                ),
                visualTransformation = PasswordVisualTransformation(mask = '•')
            )
            Divider(thickness = 10.dp, color = Color.White)
            TextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Phone,
                    autoCorrect = false
                )
            )
        }
        Divider(thickness = 10.dp, color = Color.White)
        if (type == SetupType.REGISTER) {
            Button(onClick = {
                activity.registerAttempt(
                    email,
                    password,
                    confirmPassword,
                    phoneNumber
                )
            }) {
                Text(text = "Register")
            }
        } else {
            Button(onClick = { activity.loginAttempt(email, password) }) {
                Text(text = "Sign in")
            }
        }
        Divider(thickness = 10.dp, color = Color.White)
        Button(onClick = { activity.signInWithGoogle() }) {
            Text(text = "Sign in with Google")
        }
    }
}