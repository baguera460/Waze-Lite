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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
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

enum class AuthenticationType {
    LOGIN, REGISTER;
}

interface MainInterface {
    fun signInGoogle()
    fun loginAttempt(email: String, password: String)
    fun registerAttempt(
        email: String, password: String, confirmPassword: String, phoneNumber: String
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
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.revokeAccess()

        val type = intent.getStringExtra("type")?.let { AuthenticationType.valueOf(it) }
            ?: AuthenticationType.LOGIN

        setContent {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.background_main),
                    contentDescription = "background",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.matchParentSize()
                )
                SetupView(type, this@MainActivity)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            ActivityHelper.goToActivity(
                this, HomeActivity::class.java, Intent.FLAG_ACTIVITY_CLEAR_TOP
            )
        }
    }

    override fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "Authenticated Successfully", Toast.LENGTH_SHORT).show()
                ActivityHelper.goToActivity(
                    this, HomeActivity::class.java, Intent.FLAG_ACTIVITY_CLEAR_TOP, mapOf(
                        "account" to account
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
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser!!
                if (user.isEmailVerified) {
                    Toast.makeText(this, "Authenticated Successfully", Toast.LENGTH_SHORT)
                        .show()
                    Log.d("Login", "signInWithEmail:success")
                    ActivityHelper.goToActivity(
                        this, HomeActivity::class.java, Intent.FLAG_ACTIVITY_CLEAR_TOP
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
        email: String, password: String, confirmPassword: String, phoneNumber: String
    ) {
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || phoneNumber.isEmpty()) {
            Log.w("Login", "Empty fields")
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()

            return
        }
        if (password == confirmPassword) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user!!.sendEmailVerification().addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(
                                this, "Email verification sent to $email", Toast.LENGTH_LONG
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
                            "mail" to email, "phone" to phoneNumber
                        )
                    )
                    userTable.child(user.uid).setValue(userInformation)
                    ActivityHelper.goToActivity(
                        this,
                        MailVerificationActivity::class.java,
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
                    )
                } else {
                    Log.w("Register", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        this, "${task.exception?.localizedMessage}", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Log.w("Register", "Passwords do not match")
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun Title(text: String) {
    Text(
        text = text,
        textAlign = TextAlign.Center,
        color = Color.Black,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun MainButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .defaultMinSize(minWidth = 200.dp)
            .padding(5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF68B7FF), contentColor = Color.White
        )
    ) {
        Text(text = text, fontSize = 20.sp)
    }
}

@Composable
fun Input(
    value: String,
    text: String,
    type: KeyboardType,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onValueChange: (String) -> Unit
) {
    var visualTransformationRemember by remember { mutableStateOf(visualTransformation) }
    var eyeIconId by remember { mutableIntStateOf(R.drawable.eye_closed) }

    Column {
        Text(
            text = text,
            fontSize = 20.sp,
            color = Color(0xFF000000),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start,
            modifier = Modifier.width(TextFieldDefaults.MinWidth)
        )
        TextField(value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = type, autoCorrect = false
            ),
            visualTransformation = visualTransformationRemember,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFFFFFFF),
                unfocusedContainerColor = Color(0xFFFFFFFF),
                focusedTextColor = Color(0xFF000000),
                unfocusedTextColor = Color(0xFF000000),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTrailingIconColor = Color(0xFF000000),
                unfocusedTrailingIconColor = Color(0xFF000000)
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.border(2.dp, Color(0xFF93B1FF), RoundedCornerShape(20.dp)),
            textStyle = TextStyle(fontSize = 17.sp),
            trailingIcon = {
                if (value.isNotEmpty()) {
                    Row {
                        if (text.contains("Password", true)) {
                            IconButton(onClick = {
                                if (eyeIconId == R.drawable.eye_opened) {
                                    visualTransformationRemember =
                                        PasswordVisualTransformation(mask = '•')
                                    eyeIconId = R.drawable.eye_closed
                                } else {
                                    visualTransformationRemember = VisualTransformation.None
                                    eyeIconId = R.drawable.eye_opened
                                }
                            }) {
                                Icon(
                                    painter = painterResource(id = eyeIconId),
                                    contentDescription = "eye icon",
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                        IconButton(onClick = { onValueChange("") }) {
                            Icon(
                                imageVector = Icons.Filled.Clear, contentDescription = "Clear"
                            )
                        }
                    }
                }
            })
    }
}

@Composable
fun SetupView(type: AuthenticationType, activity: MainActivity) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (type == AuthenticationType.REGISTER) Title(text = "Sign up")
        else Title(text = "Sign in")

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Input(
                value = email, text = "Email", type = KeyboardType.Email
            ) {
                email = it
            }
            Input(
                value = password,
                text = "Password",
                type = KeyboardType.Password,
                visualTransformation = PasswordVisualTransformation(mask = '•')
            ) {
                password = it
            }
            if (type == AuthenticationType.REGISTER) {
                Input(
                    value = confirmPassword,
                    text = "Confirm Password",
                    type = KeyboardType.Password,
                    visualTransformation = PasswordVisualTransformation(mask = '•')
                ) {
                    confirmPassword = it
                }
                Input(
                    value = phoneNumber, text = "Phone Number", type = KeyboardType.Phone
                ) {
                    phoneNumber = it
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (type == AuthenticationType.REGISTER) {
                    MainButton(
                        "Sign up"
                    ) {
                        activity.registerAttempt(
                            email, password, confirmPassword, phoneNumber
                        )
                    }
                    MainButton(text = "Sign in") {
                        ActivityHelper.goToActivity(
                            activity, MainActivity::class.java, extras = mapOf(
                                "type" to AuthenticationType.LOGIN.name
                            )
                        )
                    }
                } else {
                    MainButton(text = "Sign in") {
                        activity.loginAttempt(email, password)
                    }
                    MainButton(text = "Sign up") {
                        ActivityHelper.goToActivity(
                            activity, MainActivity::class.java, extras = mapOf(
                                "type" to AuthenticationType.REGISTER.name
                            )
                        )
                    }
                    TextButton(onClick = {
                        ActivityHelper.goToActivity(
                            activity, PasswordResetActivity::class.java
                        )
                    }) {
                        Text(
                            text = "Forgot your password ?",
                            fontSize = 15.sp,
                            textDecoration = TextDecoration.Underline,
                            color = Color(0xFF0085FF)
                        )
                    }
                }
            }
        }
        if (type == AuthenticationType.LOGIN) {
            Button(
                onClick = { activity.signInGoogle() },
                modifier = Modifier.padding(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF68B7FF), contentColor = Color.White
                )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google_logo),
                    contentDescription = "Google Logo",
                    alignment = Alignment.CenterStart,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Sign in with Google", fontSize = 20.sp)
            }
        }
    }
}
