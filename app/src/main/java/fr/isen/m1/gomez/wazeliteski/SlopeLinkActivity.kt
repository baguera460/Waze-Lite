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
import androidx.compose.foundation.shape.CircleShape
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
            Row(modifier = Modifier.padding(5.dp, 25.dp)) {
                Text("Liste de toutes les pistes\n")
            }
            val nexts = slope?.next
            if (nexts != null) {
                for (i : Int in nexts)
                    GetNextSlopes(index = i, slopes)
            }
            Text(slopes.toList().toString())
            for (s : Slope in slopes)
                TextButton(onClick = {
                    val intent = Intent(context, SlopeLinkActivity::class.java)
                    intent.putExtra(SlopeLinkActivity.SLOPE_EXTRA_KEY, s)
                    context.startActivity(intent)
                },
                    modifier = Modifier.align(Alignment.CenterHorizontally))
                    {
                        Text(s.name)
                    }


            Text(text = if (slope?.end == true) "Piste finale !" else "")
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
fun GetNextSlopes(index: Int?, slopes: MutableList<Slope>) {
    val ref = DataBaseHelper.database.getReference("slopes/$index")
    ref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val slope = snapshot.getValue(Slope::class.java)
            if (slope != null && !slopes.contains(slope)){
                println("Slope ${index}: color=${slope.color}, name=${slope.name}")
                slopes.add(slope)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("dataBase", error.toString())
        }
    })
}