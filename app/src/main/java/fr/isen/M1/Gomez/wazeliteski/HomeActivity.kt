package fr.isen.m1.gomez.wazeliteski

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import fr.isen.m1.gomez.wazeliteski.ui.theme.WazeLiteSkiTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WazeLiteSkiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChoiceView(this)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChoiceView(activity: HomeActivity) {
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
            )
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("You are signed in")
        Button(onClick = { ActivityHelper.signOut(activity) }) {
            Text("Sign out")
        }
        Text(text = "or")
        Button(onClick = { ActivityHelper.goToActivity(activity, ChatActivity::class.java) }) {
            Text("Chat")
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