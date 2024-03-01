package fr.isen.m1.gomez.wazeliteski

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.m1.gomez.wazeliteski.data.Level
import fr.isen.m1.gomez.wazeliteski.data.Slope

class SlopeLinkActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val slope = intent.getSerializableExtra(SLOPE_EXTRA_KEY) as? Slope
        setContent {
            LinkView(slope)
        }
    }

    companion object {
        const val SLOPE_EXTRA_KEY = "SLOPE_EXTRA_KEY"
    }
}

@Composable
fun LinkView(slope: Slope?) {
    val color: Color? = (slope?.color?.let { Level.from(it) })?.colorId()
    val colorstate: Color = if (slope?.state == true) Color(20, 200, 20) else Color(220, 20, 20)
    val state: String = if (slope?.state == true) "ouverte" else "fermée"
    Column() {
        if (color != null) {
            TopBar("Desservissement ${slope.name}", color)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(176, 196, 222))
        ) {
            Row (modifier = Modifier.offset(5.dp, 5.dp)){
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(colorstate)
                )
                Text("Piste $state",
                    Modifier.padding(5.dp, 10.dp),
                    fontSize = 20.sp)
            }
            Row(modifier = Modifier.padding(5.dp, 25.dp)) {
                Text("Liste de toutes les pistes")
            }
            Text(text = if (slope?.end == true) "Piste finale !" else "" )
            Divider(modifier = Modifier.padding(0.dp, 15.dp), color = Color.Black)
            TextButton(onClick = { /*TODO*/ },
                modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Laisser un commentaire")
            }
            Row (modifier = Modifier.padding(0.dp, 15.dp)){
                Text("Liste commentaires")
            }
            Text(text = slope?.next.toString())
        }
    }
}