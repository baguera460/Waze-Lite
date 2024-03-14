package fr.isen.m1.gomez.wazeliteski

import android.content.Intent
import android.os.Bundle
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import fr.isen.m1.gomez.wazeliteski.ui.theme.WazeLiteSkiTheme

interface MailVerificationInterface {
    fun sendVerificationEmail()
}

class MailVerificationActivity : ComponentActivity(), MailVerificationInterface {
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        val user = auth.currentUser
        if (user == null || user.isEmailVerified) {
            finish()
            return
        }
        this.user = user
        auth.signOut()
        setContent {
            WazeLiteSkiTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    MailVerificationView(this, user.email!!)
                }
            }
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MailVerificationView(activity: MailVerificationActivity, email: String) {
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
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Mail Verification",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 30.sp
        )
        Text(
            text = email,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 20.sp
        )
        Button(
            modifier = Modifier.fillMaxWidth(3 / 4f),
            onClick = { activity.sendVerificationEmail() }
        ) {
            Text("Send verification email")
        }
        Button(
            modifier = Modifier.fillMaxWidth(3 / 4f),
            onClick = { ActivityHelper.goToActivity(activity, MainActivity::class.java, Intent.FLAG_ACTIVITY_CLEAR_TOP) }
        ) {
            Text("Sign In")
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