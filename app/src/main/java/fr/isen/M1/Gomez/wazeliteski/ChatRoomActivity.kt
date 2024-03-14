package fr.isen.m1.gomez.wazeliteski

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fr.isen.m1.gomez.wazeliteski.data.ChatMessage
import fr.isen.m1.gomez.wazeliteski.data.User
import fr.isen.m1.gomez.wazeliteski.ui.theme.WazeLiteSkiTheme


class ChatRoomActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val roomId = intent.getStringExtra("roomId")?.toInt() ?: 0

        auth = Firebase.auth
        database = Firebase.database
        val chatRef = database.reference.child("chat").child(roomId.toString())

        chatRef.get()
            .addOnSuccessListener {
                val messageItems = it.getValue<MutableList<ChatMessage>>() ?: mutableListOf()
                setContent {
                    WazeLiteSkiTheme {
                        Surface(
                            color = MaterialTheme.colorScheme.background
                        ) {
                            ChatRoomView(
                                this@ChatRoomActivity,
                                messageItems,
                                auth,
                                database,
                                chatRef
                            )
                        }
                    }
                }
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(activity: ChatRoomActivity) {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxScope.Footer() {
    CenterAlignedTopAppBar(
        title = {

        },
        colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier
            .clip(
                shape = RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp),
            )
            .align(Alignment.BottomStart)
    )

}

@Composable
fun ChatItem(item: ChatMessage, auth: FirebaseAuth, database: FirebaseDatabase) {
    if (item.uid!! == auth.currentUser!!.uid) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp, 0.dp),
            contentAlignment = Alignment.TopEnd,
        ) {
            Column {
                Text(
                    text = item.message!!,
                    modifier = Modifier
                        .background(
                            Color(0xFF00FF89),
                            RoundedCornerShape(10.dp, 10.dp, 4.dp, 10.dp)
                        )
                        .padding(8.dp)
                )
            }
        }
    } else {
        val user = database.reference.child("user").child(item.uid)
            .get().result?.getValue<User>()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp, 0.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Text(
                text = user!!.email!!
            )
            Text(
                text = item.message!!,
                modifier = Modifier
                    .background(
                        Color(0xFF00ABFF),
                        RoundedCornerShape(10.dp, 10.dp, 10.dp, 4.dp)
                    )
                    .padding(8.dp)
            )
        }
    }
}

fun addNewMessages(messageItems: MutableList<ChatMessage>, chatRef: DatabaseReference) {
    chatRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val chatMessages = dataSnapshot.getValue<List<ChatMessage>>()

            if (chatMessages == null) {
                Log.w("Fail", "Value read is null.")
            } else if (chatMessages.size > messageItems.size) {
                chatMessages.subList(messageItems.size, chatMessages.size).forEach {
                    messageItems.add(it)
                    Log.d("TEST", "Value added: $it")
                }
                Log.d("Success", "Value is: $messageItems")
            } else if (chatMessages.size < messageItems.size) {
                messageItems.subList(chatMessages.size, messageItems.size).forEach {
                    messageItems.remove(it)
                    Log.d("TEST", "Value removed: $it")
                }
                Log.d("Success", "Value is: $messageItems")
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("Fail", "Failed to read value.", error.toException())
        }
    })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatRoomView(
    activity: ChatRoomActivity,
    chatMessages: MutableList<ChatMessage>,
    auth: FirebaseAuth,
    database: FirebaseDatabase,
    chatRef: DatabaseReference
) {
    val messageItems = remember(chatMessages) { chatMessages }

    Box(
        modifier = Modifier.fillMaxSize()
    )
    {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            stickyHeader {
                Header(activity)
            }
            items(messageItems) {
                ChatItem(it, auth, database)
            }
        }
        Footer()
    }

    addNewMessages(messageItems, chatRef).let {
        Log.d("Success", "Value is: $messageItems")
        Log.d("Success", "Value is: $chatMessages")
    }
}