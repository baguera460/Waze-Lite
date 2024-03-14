package fr.isen.m1.gomez.wazeliteski

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fr.isen.m1.gomez.wazeliteski.data.Lift
import fr.isen.m1.gomez.wazeliteski.ui.theme.WazeLiteSkiTheme

interface ChatInterface {
}

class ChatActivity : ComponentActivity(), ChatInterface {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Firebase.database.reference.child("liftes").get().addOnSuccessListener {
            setContent {
                WazeLiteSkiTheme {
                    Surface(
                        color = MaterialTheme.colorScheme.background
                    ) {
                        it.getValue<MutableList<Lift>>()?.let { it1 -> ChatView(this, it1) }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatView(activity: ChatActivity, liftTable: MutableList<Lift>) {
    Column {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "WazeLiteSki",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 40.sp
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier
                .clip(
                    shape = RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp),
                ),
            navigationIcon = {
                IconButton(onClick = { ActivityHelper.goBack(activity) }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            userScrollEnabled = true
        ) {
            items(liftTable) { lift ->
                Button(onClick = {
                    ActivityHelper.goToActivity(
                        activity,
                        ChatRoomActivity::class.java,
                        extras = HashMap(
                            mapOf(
                                "roomId" to lift.index
                            )
                        )
                    )
                }) {
                    Text(text = lift.name)
                }
            }
        }
        Box(
            Modifier.fillMaxSize(),
            Alignment.BottomCenter
        ) {
            CenterAlignedTopAppBar(
                title = {

                },
                colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier
                    .clip(
                        shape = RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp),
                    )
            )
        }
    }
}