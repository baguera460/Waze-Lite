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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
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
            Column {
                TopBar()
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
                }
                GetDBData(slopes)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    val context = LocalContext.current
    CenterAlignedTopAppBar(
        title = {
            Text(
                "Pistes", fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(30.dp),
            )
        }, colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color(176, 196, 255)
        ), navigationIcon = {
            IconButton(onClick = {
                val intent = Intent(context, MenuActivity::class.java)
                context.startActivity(intent)
            })
            {
                Icon(
                    imageVector = Icons.Filled.Home, contentDescription = "Home"
                )
            }
        }
    )
}

@Composable
fun SlopeRow(slope: Slope) {
    val context = LocalContext.current
    var col = Color.Black
    var colcontainer = Color(0, 0, 0)
    if (slope.state) col = Color(20, 200, 20) else col = Color(220, 20, 20)
    if (slope.state) colcontainer = Color(22, 164, 7) else colcontainer = Color(200, 20, 20)
    Row {
        TextButton(onClick = {
            val intent = Intent(context, SlopeLinkActivity::class.java)
            intent.putExtra(SlopeLinkActivity.SLOPE_EXTRA_KEY, slope.name)
            context.startActivity(intent)
        }) {
            Text(
                "  " + slope.name,
                modifier = Modifier
                    .padding(5.dp, 10.dp)
                    .fillMaxWidth(0.70f),
                fontSize = 18.sp, fontWeight = FontWeight.ExtraBold,
                color = (Level.from(slope.color)).colorId()
            )

        }
        TextButton(
            onClick = {val newValue = !slope.state
                Firebase.database.reference.child("slopes/${slope.index}/state").setValue(newValue)
                slope.state = newValue },
            modifier = Modifier
                .align(alignment = Alignment.CenterVertically)
                .fillMaxWidth(),
            shape = CircleShape,
            border = BorderStroke(2.dp, col),
            colors = ButtonDefaults.buttonColors(containerColor = colcontainer)
        ) {
            Text(
                if(slope.state) { "Ouverte" } else { "Fermée" },
                color = Color.Black
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