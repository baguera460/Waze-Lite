package fr.isen.m1.gomez.wazeliteski

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
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
                LazyColumn(modifier = Modifier.fillMaxSize().background(Color(176, 196, 222))) {
                    items(slopes.toList()) {
                        SlopeRow(it)
                    }
                }
                GetDBData(slopes)
            }
        }
    }
}


@Composable
fun SlopeRow(slope : Slope){
    Row {
        Text((slope.color.toString() + " " + (slope.name)),
            modifier = Modifier.padding(5.dp, 5.dp),
            fontSize = 15.sp, fontWeight = FontWeight.Bold)
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