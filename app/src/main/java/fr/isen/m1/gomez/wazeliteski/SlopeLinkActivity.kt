package fr.isen.m1.gomez.wazeliteski

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fr.isen.m1.gomez.wazeliteski.data.Level
import fr.isen.m1.gomez.wazeliteski.data.Slope
import fr.isen.m1.gomez.wazeliteski.database.DataBaseHelper

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

    val slopes = remember {
        mutableStateListOf<Slope>()
    }
    val context = LocalContext.current

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
                .verticalScroll(rememberScrollState())
                .background(Color(176, 196, 222))
        ) {
            Row(modifier = Modifier.offset(5.dp, 5.dp)) {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(colorstate)
                )
                Text(
                    "Piste $state",
                    Modifier.padding(5.dp, 10.dp),
                    fontSize = 20.sp
                )
            }
            val nexts = slope?.next
            if (nexts != null) {
                for (i: Int in nexts)
                    GetNextSlopes(index = i, slopes)
            }
            if (slopes.isNotEmpty()) {
                Row(modifier = Modifier
                    .padding(5.dp, 25.dp)
                    .align(Alignment.CenterHorizontally)) {
                    Text("Pistes desservies \n",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold)
                }
                ParticularSlope(slope = slope)
                for (s: Slope in slopes) {
                    TextButton(
                        onClick = {
                            val intent = Intent(context, SlopeLinkActivity::class.java)
                            intent.putExtra(SlopeLinkActivity.SLOPE_EXTRA_KEY, s)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    {
                        Text(s.name, color = (Level.from(s.color)).colorId())
                    }
                }

            }

            Text(
                text = if (slope?.end == true && slope.next?.isEmpty() == null) "Piste finale !"
                else if (slope?.end == true) "Possible de terminer la descente sur cette piste"
                else ""
            )
            Divider(modifier = Modifier.padding(0.dp, 15.dp), color = Color.Black)
            TextButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Laisser un commentaire")
            }
            Row(modifier = Modifier.padding(0.dp, 15.dp)) {
                Text("Liste commentaires")
            }
        }
    }
}


@Composable
fun ParticularSlope(slope: Slope?) {
    if (slope?.name == "Le forest") {
        Text(
            "Attention ! \nSi vous provenez de la piste \"Bouticari vert\",\n" +
                    "Vous arriverez à la fin de la piste \"Le forest\""
        )
    }
    if (slope?.name == "Les jockeys")
        Text(
            "Attention ! \nSi vous provenez de la piste  \"Les chamois\",\n" +
                    "Vous arriverez à la fin de la piste \"Les jockeys\""
        )
    if (slope?.name == "Pré méan")
        Text(
            "Attention ! \nSi vous provenez de la piste \"Le gourq\",\n" +
                    "Vous arriverez à la fin de la piste \"Pré méan\""
        )
    if (slope?.name == "Le gourq")
        Text(
            "Attention ! \nSi vous provenez de la piste \"La mandarine\",\n" +
                    "Vous ne pourrez pas rejoindre la piste \"La mandarine\""
        )
    if (slope?.name == "Les lampions")
        Text(
            "Attention ! \nSi vous provenez de la piste \"La mandarine\",\n" +
                    "Vous ne pourrez rejoindre que les pistes \"Le S du chamois\","+
                    "\" Les jockeys \" ou \" Le chamois \""
        )
}

@Composable
fun GetNextSlopes(index: Int?, slopes: MutableList<Slope>) {
    val ref = DataBaseHelper.database.getReference("slopes/$index")
    ref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val slope = snapshot.getValue(Slope::class.java)
            if (slope != null && !slopes.contains(slope)) {
                println("Slope ${index}: color=${slope.color}, name=${slope.name}")
                slopes.add(slope)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("dataBase", error.toString())
        }
    })
}