package fr.isen.m1.gomez.wazeliteski
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.m1.gomez.wazeliteski.data.Lift

class LiftLinkActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val lift = intent.getSerializableExtra(LIFT_EXTRA_KEY) as? Lift
        setContent {
            LinkView(lift)
        }
    }
    companion object {
        const val LIFT_EXTRA_KEY = "LIFT_EXTRA_KEY"
    }
}

@Composable
fun LinkView(lift: Lift?) {
    val state: String = if (lift?.state == true) "ouverte" else "fermée"
    Column() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(176, 196, 222))
        ) {
            Row(modifier = Modifier.offset(5.dp, 5.dp)) {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                )
                Text(
                    "Remontee $state",
                    Modifier.padding(5.dp, 10.dp),
                    fontSize = 20.sp
                )
            }

        }
    }
}