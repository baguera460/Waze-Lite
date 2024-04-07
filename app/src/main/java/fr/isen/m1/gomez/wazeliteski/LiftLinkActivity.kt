package fr.isen.m1.gomez.wazeliteski

import android.content.Intent
import android.util.Log

import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ButtonDefaults
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

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import fr.isen.m1.gomez.wazeliteski.data.Level
import fr.isen.m1.gomez.wazeliteski.data.Lift
import fr.isen.m1.gomez.wazeliteski.data.LiftType
import fr.isen.m1.gomez.wazeliteski.data.OpinionLift
import fr.isen.m1.gomez.wazeliteski.data.Slope
import fr.isen.m1.gomez.wazeliteski.database.DataBaseHelper


class LiftLinkActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val lift = intent.getSerializableExtra(LIFT_EXTRA_KEY) as? Lift
        setContent {
            LinkView(lift, this@LiftLinkActivity)
        }
    }

    companion object {
        const val LIFT_EXTRA_KEY = "LIFT_EXTRA_KEY"
    }
}

@Composable
fun LinkView(lift: Lift?, activity: LiftLinkActivity) {

    val slopes = remember {
        mutableStateListOf<Slope>()
    }

    val liftState = remember { mutableStateOf(lift?.state ?: false) }
    val state: String = if (liftState.value) "OUVERTE" else "FERMÉE"
    var colorstate by remember { mutableStateOf(if (lift?.state == true) Color(0xFF00BD41) else Color(238, 144, 144)) }
    Text(colorstate.toString())
    val liftType = LiftType.from(lift?.type ?: "")
    var text by remember {
        mutableStateOf("")
    }

    val opinionsLift = remember {
        mutableStateListOf<OpinionLift>()
    }
    val maxid = opinionsLift.size


    Column(Modifier.background(Color(0xFFD9EAF6))) {
        Header(activity)

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
                Image(
                    painter = painterResource(id = liftType.drawableId()),
                    contentDescription = "Lift Type Icon",
                    modifier = Modifier.size(60.dp)
                )
                lift?.name?.let {
                    Text(
                        it,
                        Modifier.padding(20.dp, 0.dp),
                        fontSize = 25.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }
            if (lift != null) {
                Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(colorstate)
                    TextButton(
                        onClick = {
                            val newValue = !liftState.value
                            Firebase.database.reference.child("liftes/${lift.index}/state")
                                .setValue(newValue)
                            liftState.value = newValue
                            colorstate = if (newValue) Color(0xFF00BD41) else Color(238, 144, 144)
                        },
                        modifier = Modifier.align(alignment = Alignment.CenterVertically),
                        shape = CircleShape,
                        border = BorderStroke(2.dp, colorstate),
                        colors = ButtonDefaults.buttonColors(containerColor = colorstate)
                    ) {
                        Text(
                            state,
                            Modifier.padding(30.dp, 0.dp),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }


            if (lift != null) {
                SlopesList(lift.slopes, slopes)
            }

            Row(Modifier.padding(0.dp, 20.dp)) {}
            Divider(
                Modifier
                    .width(250.dp)
                    .align(Alignment.CenterHorizontally), color = Color.Black
            )
            Spacer(Modifier.padding(0.dp, 20.dp))

            Column(Modifier.padding(5.dp)) {
                for (op: OpinionLift in opinionsLift.toList()) {
                    Column(
                        Modifier.fillMaxWidth()
                    ) {
                        if (op.lift == lift?.name) {
                            Text(
                                op.user, fontSize = 16.sp, fontWeight = FontWeight.Bold
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
            Spacer(Modifier.weight(1f))
            Row(Modifier.padding(5.dp, 5.dp)) {
                Input(
                    value = text,
                    text = "Laissez un commentaire",
                    type = KeyboardType.Text,
                ) {
                    text = it
                }
                IconButton(
                    onClick = {
                        if (text != "") {
                            Firebase.database.reference.child("opinion_lift/${maxid}/")
                                .setValue(lift?.name?.let {
                                    OpinionLift(
                                        maxid, text, it, currentUser
                                    )
                                })
                        }
                        text = ""
                    }, modifier = Modifier.absoluteOffset(y = 30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = "Send",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
        GetOpinionLift(opinionsLift)
    }
}

@Composable
fun SlopesList(next: List<Int>, slopes: MutableList<Slope>) {
    val context = LocalContext.current
    Column(Modifier.padding(0.dp, 10.dp)) {
        if (next.isNotEmpty()) {
            Box (Modifier.fillMaxWidth(), Alignment.Center) {
                Text("Descentes desservies \n",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                )
            }


            for (i: Int in next)
                GetNextSlopesForLifts(index = i, slopes = slopes)

            for (s: Slope in slopes) {
                Row {
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
    }
}


@Composable
fun GetOpinionLift(opinions: SnapshotStateList<OpinionLift>) {
    FirebaseDatabase.getInstance().getReference("opinion_lift")
        .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var index = 0
                val fireBaseOpinions = snapshot.children.mapNotNull {
                    val opinion = it.getValue(OpinionLift::class.java)
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
fun GetNextSlopesForLifts(index: Int?, slopes: MutableList<Slope>) {
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