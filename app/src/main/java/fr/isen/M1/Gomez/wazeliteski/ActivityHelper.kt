package fr.isen.m1.gomez.wazeliteski


import android.content.Intent
import androidx.activity.ComponentActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

interface ActivityHelper {
    companion object {
        fun signOut(componentActivity: ComponentActivity) {
            Firebase.auth.signOut()
            goToActivity(componentActivity, MainActivity::class.java, Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        fun goToActivity(
            componentActivity: ComponentActivity,
            activityClass: Class<*>,
            flags: Int = 0,
            extras: HashMap<String, *>? = null
        ) {
            val intent = Intent(componentActivity, activityClass)
            intent.setFlags(flags)
            if (extras != null) {
                for ((key, value) in extras) {
                    intent.putExtra(key, value.toString())
                }
            }
            componentActivity.startActivity(intent)
        }

        fun goBack(componentActivity: ComponentActivity) {
            componentActivity.finish()
        }
    }
}