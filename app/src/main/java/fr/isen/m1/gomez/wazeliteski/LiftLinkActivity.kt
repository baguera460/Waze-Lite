package fr.isen.m1.gomez.wazeliteski

import android.content.Intent
import android.util.Log

import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Arrangement

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.Firebase
import com.google.firebase.database.database
import fr.isen.m1.gomez.wazeliteski.data.Lift
import fr.isen.m1.gomez.wazeliteski.data.LiftType
import fr.isen.m1.gomez.wazeliteski.data.OpinionLift
import fr.isen.m1.gomez.wazeliteski.data.Slope
import java.util.logging.Level


class LiftLinkActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val lift = intent.getSerializableExtra(LIFT_EXTRA_KEY) as? Lift
        setContent {
            LinkView(lift)
        }
    }

    companion object {
        const val LIFT_EXTRA_KEY = "LIFT_EXTRA_KEY"
    }
}

@Composable
fun LinkView(lift: Lift?) {
    var state by remember { mutableStateOf(lift?.state ?: false) }
    val liftType = LiftType.from(lift?.type ?: "")
    var text by remember {
                mutableStateOf("")
            }
    val opinions_lift = remember {
                mutableStateListOf<OpinionLift>()
            }
    var maxid = 0
    var pres = 0
    for (op: OpinionLift in opinions_lift.toList()) {
        maxid = if (maxid < op.id) op.id else maxid
        if (op.lift == lift?.name)
            pres = 1
    }


    Column(modifier = Modifier.fillMaxSize()) {
        TopBar("Information de ${lift?.name}", Color(176, 196, 255))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                "Type de remontée: ${lift?.type}",
                Modifier.padding(bottom = 8.dp)
            )
            Image(
                painter = painterResource(id = liftType.drawableId()),
                contentDescription = "Lift Type Icon",
                modifier = Modifier
                    .size(100.dp)
            )
            Text(
                "Remontée ${if (state) "ouvrie" else "fermée"}",
                Modifier.padding(top = 8.dp, bottom = 8.dp)
            )

            TextButton(
                onClick = {
                    val newState = !state
                    state = newState
                    lift?.let {
                        FirebaseDatabase.getInstance().getReference("liftes/${lift.index}/state")
                            .setValue(newState)
                    }
                },
                modifier = Modifier.padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (state) Color.Red else Color.Green)
            ) {
                Text(
                    text = if (state) "Fermer" else "Ouvrir",
                    color = Color.White
                )
            }



            val lift = Lift(name = "Bouticari", slopes = listOf("Le forest",
            "Bouticari vert",
            "Bouticari bleu",
            "La rouge bouticari"), /* other properties */)
            SlopesList(lift.slopes)



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
                        val comm = lift?.let { OpinionLift(maxid + 1, text, it.name, "test@gmail.com") }
                        Firebase.database.reference.child("opinion_lift/${maxid + 1}/")
                            .setValue(comm)
                    }
                })
                {
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

            Column(Modifier
                .verticalScroll(rememberScrollState())) {
                for (op: OpinionLift in opinions_lift.toList()) {
                    Column(
                        Modifier
                            .background(Color(200, 200, 200))
                            .fillMaxWidth()
                    ) {
                        if (op.lift == lift?.name) {
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

            GetOpinionLift(opinions_lift)
        }
    }
}

@Composable
fun SlopesList(slopes: List<String>) {
    if (slopes.isNotEmpty()) {
        Text(
            "Pistes desservies:",
            Modifier.padding(bottom = 8.dp)
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(slopes) { slope ->
                Text(slope)
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
