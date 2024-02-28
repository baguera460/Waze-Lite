package fr.isen.m1.gomez.wazeliteski

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fr.isen.m1.gomez.wazeliteski.ui.theme.WazeLiteSkiTheme

enum class AuthenticationType {
    LOGIN, REGISTER;
}

interface MainInterface {
    fun signInGoogle()
    fun loginAttempt(email: String, password: String)
    fun registerAttempt(
        email: String,
        password: String,
        confirmPassword: String,
        phoneNumber: String
    )

    fun changeAuthenticationActivity(type: AuthenticationType)
}

class MainActivity : ComponentActivity(), MainInterface {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                updateUI(account!!)
            } catch (e: ApiException) {
                Log.w(TAG, "signInResult:failed with status code ${e.statusCode}")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        } else {
            val type = intent.getStringExtra("type")
                ?.let { AuthenticationType.valueOf(it) }
                ?: AuthenticationType.LOGIN

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

    override fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Authenticated Successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("account", account)
                    startActivity(intent)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", it.exception)
                    Toast.makeText(this, "Something went wrong...", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun loginAttempt(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Authenticated Successfully", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser!!
                    if (user.isEmailVerified) {
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Email not verified", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MailVerificationActivity::class.java)
                        startActivity(intent)
                    }
                } else {
                    Log.w("Login", "signInWithEmail:failure", task.exception)
                    Toast.makeText(this, "Wrong credentials", Toast.LENGTH_LONG).show()
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
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user!!.sendEmailVerification()
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "Email verification sent to $email",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Log.e("Register", "sendEmailVerification", task.exception)
                                    Toast.makeText(
                                        this,
                                        "Failed to send verification email",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                        val userTable = Firebase.database.reference.child("user")
                        val userInformation = HashMap<String, String>(
                            mapOf(
                                "mail" to email,
                                "password" to password,
                                "phone" to phoneNumber
                            )
                        )
                        userTable.child(user.uid).setValue(userInformation)
                        val intent = Intent(this, MailVerificationActivity::class.java)
                        startActivity(intent)
                    } else {
                        Log.w("Register", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            this,
                            "${task.exception?.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            Log.w("Register", "Passwords do not match")
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
        }
    }

    override fun changeAuthenticationActivity(type: AuthenticationType) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("type", type.name)
        startActivity(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupView(type: AuthenticationType, activity: MainInterface) {
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
        if (type == AuthenticationType.REGISTER) {
            Text(text = "Sign up", textAlign = TextAlign.Center, fontSize = 50.sp)
        } else {
            Text(text = "Sign in", textAlign = TextAlign.Center, fontSize = 50.sp)
        }
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
        if (type == AuthenticationType.REGISTER) {
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
        if (type == AuthenticationType.REGISTER) {
            Button(onClick = {
                activity.registerAttempt(
                    email,
                    password,
                    confirmPassword,
                    phoneNumber
                )
            }) {
                Text(text = "Sign up")
            }
            Text(text = "or", fontSize = 20.sp)
            Button(onClick = { activity.changeAuthenticationActivity(AuthenticationType.LOGIN) }) {
                Text(text = "Sign in")
            }
        } else {
            Button(onClick = { activity.loginAttempt(email, password) }) {
                Text(text = "Sign in")

            }
            Text(text = "or", fontSize = 20.sp)
            Button(onClick = { activity.changeAuthenticationActivity(AuthenticationType.REGISTER) }) {
                Text(text = "Sign up")
            }
        }
        Divider(thickness = 20.dp, color = Color.White)
        Button(onClick = { activity.signInGoogle() }) {
            Text(text = "Sign in with Google")
        }
    }
}