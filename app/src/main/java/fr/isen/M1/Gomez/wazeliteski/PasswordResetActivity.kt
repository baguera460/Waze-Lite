package fr.isen.m1.gomez.wazeliteski

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import fr.isen.m1.gomez.wazeliteski.ui.theme.WazeLiteSkiTheme

interface PasswordResetInterface {
    fun resetPassword(email: String)
}

class PasswordResetActivity : ComponentActivity(), PasswordResetInterface {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        setContent {
            WazeLiteSkiTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    PasswordResetView(this)
                }
            }
        }
    }

    override fun resetPassword(email: String) {
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email.", Toast.LENGTH_SHORT).show()
            return
        }
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("PasswordModificationActivity", "Email sent.")
                    Toast.makeText(this, "Email sent.", Toast.LENGTH_SHORT).show()
                    ActivityHelper.goToActivity(this, MainActivity::class.java, Intent.FLAG_ACTIVITY_CLEAR_TOP)
                } else {
                    Log.d("PasswordModificationActivity", "Email not sent.")
                    Toast.makeText(this, "Email not sent.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordResetView(activity: PasswordResetActivity) {
    var email by remember { mutableStateOf("") }

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
            ),
        navigationIcon = {
            IconButton(onClick = { ActivityHelper.goBack(activity) }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Reset your password",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 30.sp
        )
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
            Button(
                modifier = Modifier.fillMaxWidth(3 / 4f),
                onClick = { activity.resetPassword(email) }
            ) {
                Text(text = "Confirm")
            }
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