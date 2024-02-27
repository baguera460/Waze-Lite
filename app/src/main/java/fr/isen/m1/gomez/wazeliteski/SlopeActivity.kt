package fr.isen.m1.gomez.wazeliteski

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fr.isen.m1.gomez.wazeliteski.data.Level
import fr.isen.m1.gomez.wazeliteski.data.Slope
import fr.isen.m1.gomez.wazeliteski.database.DataBaseHelper


class SlopeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slope)
        setContent {
            val slopes = remember {
                mutableStateListOf<Slope>()
            }
            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(176, 196, 222))
                ) {
                    items(slopes.toList()) {
                        SlopeRow(it)
                        Spacer(modifier = Modifier.height(5.dp))
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
    Row {
        TextButton(onClick = {/* val intent = Intent(context, Activity::class.java)
                context.startActivity(intent) */
        }) {
            Text(
                "  " + slope.name,
                modifier = Modifier
                    .padding(5.dp, 10.dp)
                    .fillMaxWidth(0.90f),
                fontSize = 18.sp, fontWeight = FontWeight.ExtraBold,
                color = (Level.from(slope.color)).colorId()
            )
            Icon(
                    imageVector = Icons.Outlined.Info, contentDescription = "Slope open",
            modifier = Modifier
                .size(22.dp)
                .align(alignment = Alignment.CenterVertically),
            tint = Color.Black
            )
        }
        //var col = Color.Green
        //if (slope.state) Color.Green else col = Color.Red


    }
}

@Composable
fun GetDBData(slopes: SnapshotStateList<Slope>) {
    DataBaseHelper.database.getReference("slopes")
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val _slope = dataSnapshot.children.mapNotNull { it.getValue(Slope::class.java) }
                Log.d("database", slopes.toString())
                slopes.addAll(_slope)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("DB", databaseError.toString())
            }
        })
}