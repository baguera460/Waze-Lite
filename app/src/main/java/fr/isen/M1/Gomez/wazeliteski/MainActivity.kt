package fr.isen.m1.gomez.wazeliteski

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
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
        googleSignInClient.revokeAccess()

        val type = intent.getStringExtra("type")
            ?.let { AuthenticationType.valueOf(it) }
            ?: AuthenticationType.LOGIN

        setContent {
            WazeLiteSkiTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainView(type, this)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            ActivityHelper.goToActivity(
                this,
                HomeActivity::class.java,
                Intent.FLAG_ACTIVITY_CLEAR_TOP
            )
        }
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
                    ActivityHelper.goToActivity(
                        this,
                        HomeActivity::class.java,
                        Intent.FLAG_ACTIVITY_CLEAR_TOP,
                        HashMap(
                            mapOf(
                                "account" to account
                            )
                        )
                    )
                } else {
                    Log.w(TAG, "signInWithCredential:failure", it.exception)
                    Toast.makeText(this, "Something went wrong...", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun loginAttempt(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            Log.w("Login", "Empty fields")
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()

            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser!!
                    if (user.isEmailVerified) {
                        Toast.makeText(this, "Authenticated Successfully", Toast.LENGTH_SHORT)
                            .show()
                        ActivityHelper.goToActivity(
                            this,
                            HomeActivity::class.java,
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                        )
                    } else {
                        Toast.makeText(this, "Email not verified", Toast.LENGTH_SHORT).show()
                        ActivityHelper.goToActivity(
                            this,
                            MailVerificationActivity::class.java,
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                        )
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
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || phoneNumber.isEmpty()) {
            Log.w("Login", "Empty fields")
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()

            return
        }
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
                                "phone" to phoneNumber
                            )
                        )
                        userTable.child(user.uid).setValue(userInformation)

                        ActivityHelper.goToActivity(
                            this,
                            MailVerificationActivity::class.java,
                        )
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(type: AuthenticationType, activity: MainActivity) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "WazeLiteSki",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 40.sp
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier
            .clip(
                shape = RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp),
            )
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (type == AuthenticationType.REGISTER) {
            Text(
                text = "Sign up",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 30.sp
            )
        } else {
            Text(
                text = "Sign in",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 30.sp
            )
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                    Button(onClick = {
                        ActivityHelper.goToActivity(
                            activity,
                            activity::class.java,
                            extras = HashMap(
                                mapOf(
                                    "type" to AuthenticationType.LOGIN.name
                                )
                            )
                        )
                    }) {
                        Text(text = "Sign in")
                    }
                } else {
                    Button(onClick = { activity.loginAttempt(email, password) }) {
                        Text(text = "Sign in")
                    }
                    Text(text = "or", fontSize = 20.sp)
                    Button(onClick = {
                        ActivityHelper.goToActivity(
                            activity,
                            activity::class.java,
                            extras = HashMap(
                                mapOf(
                                    "type" to AuthenticationType.REGISTER.name
                                )
                            )
                        )
                    }) {
                        Text(text = "Sign up")
                    }
                    TextButton(onClick = {
                        ActivityHelper.goToActivity(
                            activity,
                            PasswordResetActivity::class.java
                        )
                    }) {
                        Text(text = "Forgot your password?")
                    }
                }
            }
        }
        Button(onClick = { activity.signInGoogle() }) {
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.google_logo),
                contentDescription = "Google Logo",
            )
            Text(text = " Sign in with Google")
        }
    }
    Box(
        Modifier.fillMaxSize(),
        Alignment.BottomCenter
    ) {
        CenterAlignedTopAppBar(
            title = {

            },
            colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier
                .clip(
                    shape = RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp),
                )
        )
    }
}
