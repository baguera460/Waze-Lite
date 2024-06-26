package fr.isen.m1.gomez.wazeliteski

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import fr.isen.m1.gomez.wazeliteski.data.Level
import fr.isen.m1.gomez.wazeliteski.data.Slope
import fr.isen.m1.gomez.wazeliteski.database.DataBaseHelper

class SlopeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val slopes = remember {
                mutableStateListOf<Slope>()
            }
            Column (modifier = Modifier.background(Color(0xFFD9EAF6))) {
                Header(this@SlopeActivity)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFD9EAF6)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Pistes",
                        modifier = Modifier
                            .padding(vertical = 20.dp)
                            .background(Color(0xFFD9EAF6)),
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    )
                }
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        modifier = Modifier.background(Color(0xFFD9EAF6))
                    ) {
                        items(slopes.toList()) {
                            SlopeRow(it)
                            Spacer(modifier = Modifier.height(5.dp))
                        }
                    }
                }
                GetDBData(slopes)
            }
        }
    }
}

@Composable
fun SlopeRow(slope: Slope) {
    val context = LocalContext.current
    val col: Color = if (slope.state) Color(20, 200, 20) else Color(220, 20, 20)
    val container: Color = if (slope.state) Color(22, 164, 7) else Color(200, 20, 20)
    Row {
        TextButton(onClick = {
            val intent = Intent(context, SlopeLinkActivity::class.java)
            intent.putExtra(SlopeLinkActivity.SLOPE_EXTRA_KEY, slope)
            context.startActivity(intent)
        }) {
            Box(
                modifier = Modifier
                    .graphicsLayer(alpha = if (slope.state) 1f else 0.25f)
                    .size(35.dp)
                    .background((Level.from(slope.color)).colorId(),
                        shape = RoundedCornerShape(30.dp)
                    )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                slope.name,
                modifier = Modifier
                    .padding(5.dp, 10.dp)
                    .fillMaxWidth(0.70f)
                    .graphicsLayer(alpha = if (slope.state) 1f else 0.25f),
                fontSize = 18.sp, fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
        }
        TextButton(
            onClick = {val newValue = !slope.state
                Firebase.database.reference.child("slopes/${slope.index}/state").setValue(newValue)
                slope.state = newValue },
            modifier = Modifier
                .graphicsLayer(alpha = if (slope.state) 1f else 0.50f)
                .align(alignment = Alignment.CenterVertically)
                .fillMaxWidth(0.95f),
            shape = CircleShape,
            border = BorderStroke(2.dp, col),
            colors = ButtonDefaults.buttonColors(containerColor = container)
        ) {
            Text(
                if(slope.state) { "Ouverte" } else { "Fermée" },
                color = Color.White
            )
        }

    }
}

@Composable
fun GetDBData(slopes: SnapshotStateList<Slope>) {
    DataBaseHelper.database.getReference("slopes")
        .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var index = 0
                val fireBaseSlopes = snapshot.children.mapNotNull {
                    val slope = it.getValue(Slope::class.java)
                    slope?.index = index
                    index += 1
                    return@mapNotNull slope
                }
                slopes.removeAll { true }
                slopes.addAll(fireBaseSlopes)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("dataBase", error.toString())
            }
        })
}