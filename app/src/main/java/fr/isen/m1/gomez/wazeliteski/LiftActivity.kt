package fr.isen.m1.gomez.wazeliteski


import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.google.firebase.database.database
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fr.isen.m1.gomez.wazeliteski.data.Lift
import fr.isen.m1.gomez.wazeliteski.database.DataBaseHelper

class LiftActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            val liftes = remember {
                mutableStateListOf<Lift>()
            }
            Column {
                TopBar("Remontee", Color(176, 196, 255))
                Surface(modifier = Modifier.fillMaxSize())
                {
                    LazyColumn(
                        modifier = Modifier
                        .fillMaxSize()
                        .background(Color(176, 196, 222))
                    ) {
                        items(liftes.toList()) {
                            LiftRow(it)
                            Spacer(modifier = Modifier.height(5.dp))
                        }
                    }
                    GetDBData(liftes)
                }
            }

        }
    }
}

@Composable
fun LiftRow(lift: Lift) {
    val context = LocalContext.current
    val col: Color = if (lift.state) Color(20, 200, 20) else Color(220, 20, 20)
    val container: Color = if (lift.state) Color(22, 164, 7) else Color(200, 20, 20)
    Row {
        TextButton(onClick = {
            val intent = Intent(context, LiftLinkActivity::class.java)
            intent.putExtra(LiftLinkActivity.LIFT_EXTRA_KEY, lift)
            context.startActivity(intent)
        }) {
            Text(
                "  " + lift.name,
                modifier = Modifier
                    .padding(5.dp, 10.dp)
                    .fillMaxWidth(0.70f),
                fontSize = 18.sp, fontWeight = FontWeight.ExtraBold,
            )

        }
        TextButton(
            onClick = {val newValue = !lift.state
                Firebase.database.reference.child("liftes/${lift.index}/state").setValue(newValue)
                lift.state = newValue },
            modifier = Modifier
                .align(alignment = Alignment.CenterVertically)
                .fillMaxWidth(),
            shape = CircleShape,
            border = BorderStroke(2.dp, col),
            colors = ButtonDefaults.buttonColors(containerColor = container)
        ) {
            Text(
                if(lift.state) { "Ouverte" } else { "Fermée" },
                color = Color.Black
            )
        }

    }
}

@Composable
fun GetDBData(slopes: SnapshotStateList<Lift>) {
    DataBaseHelper.database.getReference("liftes")
        .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var index = 0
                val fireBaseSlopes = snapshot.children.mapNotNull {
                    val slope = it.getValue(Lift::class.java)
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
