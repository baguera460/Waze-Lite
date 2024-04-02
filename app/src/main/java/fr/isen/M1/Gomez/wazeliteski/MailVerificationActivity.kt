package fr.isen.m1.gomez.wazeliteski

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

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
            ActivityHelper.goToActivity(
                this,
                MainActivity::class.java,
                Intent.FLAG_ACTIVITY_CLEAR_TOP
            )
            return
        }
        this.user = user
        auth.signOut()
        setContent {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.background_main_darker),
                    contentDescription = "background",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.matchParentSize()
                )
                MailVerificationView(this@MailVerificationActivity)
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

@Composable
fun MailVerificationView(activity: MailVerificationActivity) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.mail_verification),
            contentDescription = "Mail Verification"
        )
        Text(
            text = "Connect to your email address",
            textAlign = TextAlign.Center,
            color = Color.Black,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "A verification email has been sent to your email address. Once you have verified your email address, click on the button below to sign in.",
            textAlign = TextAlign.Center,
            color = Color.Black,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Text(
            text = "If you didn't receive the email, click on the button below to send it again.",
            textAlign = TextAlign.Center,
            color = Color.Black,
            fontSize = 15.sp,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        MainButton(text = "Send verification email") {
            activity.sendVerificationEmail()
        }
        MainButton(text = "Sign In") {
            ActivityHelper.goToActivity(
                activity,
                MainActivity::class.java,
                Intent.FLAG_ACTIVITY_CLEAR_TOP
            )
        }
    }
}