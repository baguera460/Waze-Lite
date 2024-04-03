package fr.isen.m1.gomez.wazeliteski

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import fr.isen.m1.gomez.wazeliteski.data.Level
import fr.isen.m1.gomez.wazeliteski.data.OpinionSlope
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
    Column {
        if (color != null) {
            TopBar("Desservissement ${slope.name}", color)
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .background(Color(176, 196, 222))
        ) {
            Row(modifier = Modifier.padding(5.dp, 10.dp)) {
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
            Divider(color = Color.Black)
            val nexts = slope?.next
            if (nexts != null) {
                for (i: Int in nexts)
                    GetNextSlopes(index = i, slopes)
            }
            if (slope?.end == true || slope?.next?.isEmpty() == null)
                Row(Modifier.padding(0.dp, 15.dp)) {
                    if (slope?.end == true && slope.next?.isEmpty() == null)
                        Image(
                            painterResource(id = R.drawable.end_slope), null,
                            Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(20.dp))
                        )
                    if (slope?.end == true && slope.next?.isEmpty() == null)
                        Text(
                            text = "Fin de piste",
                            Modifier.padding(7.dp, 12.dp),
                            fontFamily = FontFamily(Font(R.font.notoserifmakasar_regular)),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            style = TextStyle(textDecoration = TextDecoration.Underline)
                        )
                    else if (slope?.end == true)
                        Text(
                            text = "Possible de terminer la descente sur cette piste",
                            Modifier.padding(0.dp, 7.dp),
                            fontFamily = FontFamily(Font(R.font.notoserifmakasar_regular)),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                }
            if (isparticular(slope)) {
                Row(Modifier.padding(2.dp, 20.dp)) {
                    ParticularSlope(slope = slope)
                }
            }
            if (slopes.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .padding(5.dp, 10.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        "Pistes desservies \n",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                for (s: Slope in slopes) {
                    Divider(
                        color = Color.Black,
                        modifier = Modifier
                            .width(80.dp)
                            .height(2.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    TextButton(
                        onClick = {
                            val intent = Intent(context, SlopeLinkActivity::class.java)
                            intent.putExtra(SlopeLinkActivity.SLOPE_EXTRA_KEY, s)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    {
                        Text(
                            s.name,
                            fontSize = 15.sp,
                            color = (Level.from(s.color)).colorId()
                        )
                    }
                }
                Divider(
                    color = Color.Black,
                    modifier = Modifier
                        .width(80.dp)
                        .height(2.dp)
                        .align(Alignment.CenterHorizontally)
                )

            }
            Row(Modifier.padding(0.dp, 40.dp)) {}
            var text by remember {
                mutableStateOf("")
            }

            val opinions_slope = remember {
                mutableStateListOf<OpinionSlope>()
            }
            var maxid = 0
            var pres = 0
            for (op: OpinionSlope in opinions_slope.toList()) {
                maxid = if (maxid < op.id) op.id else maxid
                if (op.slope == slope?.name)
                    pres = 1
            }
            Row(Modifier.padding(0.dp, 5.dp)) {
                TextField(
                    value = text, onValueChange = { text = it },
                    label = { Text("Laisser un commentaire") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(200, 200, 200),
                        unfocusedContainerColor = Color(200, 200, 200),
                        unfocusedLabelColor = Color(155, 155, 155)
                    ),
                    modifier = Modifier.width(300.dp)
                )
                IconButton(onClick = {
                    if (text != "") {
                        val comm = slope?.let { OpinionSlope(maxid + 1, text, it.name, "test@gmail.com") }
                        Firebase.database.reference.child("opinion_slope/${maxid + 1}/")
                            .setValue(comm)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.Send, contentDescription = "Send",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(30.dp)
                    )
                }
            }

            Row(
                Modifier
                    .background(Color(200, 200, 200))
                    .fillMaxWidth()
                    .padding(0.dp, 10.dp)
            ) {
                if (pres == 1)
                    Text("Commentaires récents", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                else
                    Text("C'est calme ici...", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Column() {
                for (op: OpinionSlope in opinions_slope.toList()) {
                    Column(
                        Modifier
                            .background(Color(200, 200, 200))
                            .fillMaxWidth()
                    ) {
                        if (op.slope == slope?.name) {
                            Text(
                                "De " + op.user,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(op.comment)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            GetOpinionSlope(opinions_slope)
            //Text("Afficher plus")
        }
    }
}


@Composable
fun ParticularSlope(slope: Slope?) {
    if (slope?.name == "Le forest") {
        Column {
            Text(
                "Attention :",
                style = TextStyle(
                    textDecoration = TextDecoration.Underline,
                    color = Color.Red
                )
            )
            Text(
                "Si vous provenez de la piste \"Bouticari vert\",\n" +
                        "Vous arriverez à la fin de la piste \"Le forest\" !",
                fontFamily = FontFamily(Font(R.font.notoserifmakasar_regular))
            )
        }
    }
    if (slope?.name == "Les jockeys")
        Column {
            Text(
                "Attention :",
                style = TextStyle(
                    textDecoration = TextDecoration.Underline,
                    color = Color.Red
                )
            )
            Text(
                "Si vous provenez de la piste  \"Les chamois\",\n" +
                        "Vous arriverez à la fin de la piste \"Les jockeys\" !",
                fontFamily = FontFamily(Font(R.font.notoserifmakasar_regular))
            )
        }
    if (slope?.name == "Pré méan")
        Column {
            Text(
                "Attention :",
                style = TextStyle(
                    textDecoration = TextDecoration.Underline,
                    color = Color.Red
                )
            )
            Text(
                "Si vous provenez de la piste \"Le gourq\",\n" +
                        "Vous arriverez à la fin de la piste \"Pré méan\" !",
                fontFamily = FontFamily(Font(R.font.notoserifmakasar_regular))
            )
        }
    if (slope?.name == "Le gourq")
        Column {
            Text(
                "Attention :",
                style = TextStyle(
                    textDecoration = TextDecoration.Underline,
                    color = Color.Red
                )
            )
            Text(
                "Si vous provenez de la piste \"La mandarine\",\n" +
                        "Vous ne pourrez pas rejoindre la piste \"La mandarine\" !",
                fontFamily = FontFamily(Font(R.font.notoserifmakasar_regular))
            )
        }
    if (slope?.name == "Les lampions")
        Column {
            Text(
                "Attention :",
                style = TextStyle(
                    textDecoration = TextDecoration.Underline,
                    color = Color.Red
                )
            )
            Text(
                "Si vous provenez de la piste \"La mandarine\",\n" +
                        "Vous ne pourrez rejoindre que les pistes \"Le S du chamois\"," +
                        "\" Les jockeys \" ou \" Le chamois \" !",
                fontFamily = FontFamily(Font(R.font.notoserifmakasar_regular))
            )
        }
}

fun isparticular(slope: Slope?): Boolean {
    return (slope?.name == "Le forest" || slope?.name == "Les jockeys" || slope?.name == "Pré méan"
            || slope?.name == "Le gourq" || slope?.name == "Les lampions")
}

@Composable
fun GetNextSlopes(index: Int?, slopes: MutableList<Slope>) {
    val ref = DataBaseHelper.database.getReference("slopes/$index")
    ref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val slope = snapshot.getValue(Slope::class.java)
            if (slope != null && !slopes.contains(slope)) {
                slopes.add(slope)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("dataBase", error.toString())
        }
    })
}

fun GetOpinionSlope(opinions: SnapshotStateList<OpinionSlope>) {
    DataBaseHelper.database.getReference("opinion_slope")
        .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var index = 0
                val fireBaseOpinions = snapshot.children.mapNotNull {
                    val opinion = it.getValue(OpinionSlope::class.java)
                    opinion?.id = index
                    index += 1
                    return@mapNotNull opinion
                }
                opinions.removeAll { true }
                opinions.addAll(fireBaseOpinions)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("dataBase", error.toString())
            }
        })
}
@Composable
fun SlopeDetailsView(slope: Slope) {
    // Your UI code to display slope details
    Column {
        Text("Name: ${slope.name}")
        Text("State: ${if (slope.state) "Open" else "Closed"}")
        // Add more details as needed
    }
}
