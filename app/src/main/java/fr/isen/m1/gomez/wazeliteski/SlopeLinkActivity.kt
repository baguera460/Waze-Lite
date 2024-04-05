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
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
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
            LinkView(slope, this@SlopeLinkActivity)
        }
    }

    companion object {
        const val SLOPE_EXTRA_KEY = "SLOPE_EXTRA_KEY"
    }
}

@Composable
fun LinkView(slope: Slope?, activity: SlopeLinkActivity) {

    val slopes = remember {
        mutableStateListOf<Slope>()
    }
    val context = LocalContext.current

    val color: Color? = (slope?.color?.let { Level.from(it) })?.colorId()
    val colorstate: Color = if (slope?.state == true) Color(0xFF00BD41) else Color(238, 144, 144)
    val state: String = if (slope?.state == true) "OUVERTE" else "FERMÉE"
    Column(Modifier.background(Color(0xFFD9EAF6))) {
        if (color != null) {
            Header(activity)
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFD9EAF6))
        ) {
            Row(
                modifier = Modifier
                    .padding(5.dp, 30.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                color?.let {
                    Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(it)
                }?.let {
                    Box(
                        modifier = it
                    )
                }
                slope?.name?.let {
                    Text(
                        it,
                        Modifier.padding(30.dp, 0.dp),
                        fontSize = 25.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }
            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .background(colorstate)
                TextButton(
                    onClick = {
                        val newValue = !slope?.state!!
                        Firebase.database.reference.child("slopes/${slope.index}/state")
                            .setValue(newValue)
                        slope.state = newValue
                    },
                    modifier = Modifier
                        .align(alignment = Alignment.CenterVertically),
                    shape = CircleShape,
                    border = BorderStroke(2.dp, colorstate),
                    colors = ButtonDefaults.buttonColors(containerColor = colorstate)
                )
                {
                    Text(
                        state,
                        Modifier.padding(30.dp, 0.dp),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }
            val nexts = slope?.next
            if (nexts != null) {
                for (i: Int in nexts)
                    GetNextSlopes(index = i, slopes)
            }
            if (slope?.end == true || slope?.next?.isEmpty() == null)
                Column(
                    Modifier
                        .padding(0.dp, 10.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    if (slope?.end == true && slope.next?.isEmpty() == null)
                        TButton("Information :", "Cette descente est une fin de piste, veuillez ralentir à la fin.", Color(0xFFffc278), 95 )
                    else if (slope?.end == true)
                        TButton("Information :", "Il est possible de terminer la descente sur cette piste." ,Color(0xFFffc278), 95)
                }
            if (isparticular(slope)) {
                Column(
                    Modifier
                        .padding(0.dp, 15.dp)
                        .align(Alignment.CenterHorizontally)){
                    ParticularSlope(slope = slope)
                }
            }

            Row(Modifier.padding(0.dp, 10.dp)) {}
            if (slopes.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        "Descentes desservies \n",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                for (s: Slope in slopes) {
                    Row{
                        Box(
                            Modifier
                                .padding(10.dp)
                                .size(45.dp)
                                .clip(CircleShape)
                                .background((Level.from(s.color)).colorId())
                        )
                        Row(Modifier.padding(0.dp, 7.dp)) {
                            TextButton(
                                onClick = {
                                    val intent = Intent(context, SlopeLinkActivity::class.java)
                                    intent.putExtra(SlopeLinkActivity.SLOPE_EXTRA_KEY, s)
                                    context.startActivity(intent)
                                },
                            )
                            {
                                Text(
                                    s.name,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                )
                            }
                        }
                    }
                }
            }
            Row(Modifier.padding(0.dp, 20.dp)) {}
            Divider(
                Modifier
                    .width(250.dp)
                    .align(Alignment.CenterHorizontally),
                color = Color.Black
            )
            Spacer(Modifier.padding(0.dp, 20.dp))
            var text by remember {
                mutableStateOf("")
            }

            val opinionsSlope = remember {
                mutableStateListOf<OpinionSlope>()
            }

            val maxid = opinionsSlope.size


            Column(Modifier.padding(5.dp)) {
                for (op: OpinionSlope in opinionsSlope.toList()) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                    ) {
                        if (op.slope == slope?.name) {
                            Text(
                                op.user,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            TextButton(

                                onClick = {},
                                enabled = false,
                                shape = RoundedCornerShape(5.dp),
                                border = BorderStroke(2.dp, Color(100, 200, 100)),
                                colors = ButtonDefaults.buttonColors(
                                    disabledContainerColor = Color(100, 200, 100),
                                    disabledContentColor = Color.White
                                )
                            ) {
                                Text(op.comment)
                            }
                            Spacer(modifier = Modifier.height(18.dp))
                        }
                    }
                }
            }

            val currentUser = Firebase.auth.currentUser?.email.toString()

            Row(Modifier.padding(5.dp, 5.dp)) {
                Input(
                    value = text,
                    text = "Laissez un commentaire",
                    type = KeyboardType.Text,
                ) {
                    text = it
                }
                IconButton(onClick = {
                    if (text != "") {
                        Firebase.database.reference.child("opinion_slope/${maxid}/")
                            .setValue(slope?.name?.let {
                                OpinionSlope(
                                    maxid, text,
                                    it, currentUser
                                )
                            })
                    }
                    text = ""
                },
                    modifier = Modifier.absoluteOffset(y=30.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Send, contentDescription = "Send",
                        modifier = Modifier
                            .size(40.dp)
                    )
                }
            }
            getOpinionSlope(opinionsSlope)
        }
    }
}


@Composable
fun ParticularSlope(slope: Slope?) {
    if (slope?.name == "Le forest")
            TButton("Attention :",
                "Si vous provenez de la piste \"Bouticari vert\", " +
                        "vous arriverez à la fin de la piste \"Le forest\" !",
                Color(220,20,60), 120)
    if (slope?.name == "Les jockeys")
        TButton("Attention :",
                "Si vous provenez de la piste  \"Les chamois\", " +
                        "vous arriverez à la fin de la piste \"Les jockeys\" !",
            Color(220,20,60), 120)
    if (slope?.name == "Pré méan")
        TButton("Attention :",
                "Si vous provenez de la piste \"Le gourq\", " +
                        "vous arriverez à la fin de la piste \"Pré méan\" !",
            Color(220,20,60), 120)
    if (slope?.name == "Le gourq")
        TButton("Attention :",
                "Si vous provenez de la piste \"La mandarine\", " +
                        "vous ne pourrez pas rejoindre la piste \"La mandarine\" !",
            Color(220,20,60), 120)
    if (slope?.name == "Les lampions")
        TButton("Attention :",
                "Si vous provenez de la piste \"La mandarine\", " +
                        "vous ne pourrez rejoindre que les pistes \"Le S du chamois\"," +
                        "\" Les jockeys \" ou \" Le chamois \" !",
            Color(220,20,60), 135)
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

@Composable
fun TButton(texthead : String, text : String, col : Color, size : Int){
    TextButton(
        onClick = {},
        enabled = false,
        modifier = Modifier
            .height(size.dp)
            .fillMaxWidth(0.85f),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(2.dp, col),
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = col,
            disabledContentColor = Color.White
        )
    ) {
        Column {
            Text(
                texthead,
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.notoserifmakasar_regular)),
                fontWeight = FontWeight.Bold
            )
            Text(text, Modifier.padding(6.dp),
                fontFamily = FontFamily(Font(R.font.notoserifmakasar_regular)),
                fontWeight = FontWeight.Bold)
        }
    }
}

fun getOpinionSlope(opinions: SnapshotStateList<OpinionSlope>) {
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
