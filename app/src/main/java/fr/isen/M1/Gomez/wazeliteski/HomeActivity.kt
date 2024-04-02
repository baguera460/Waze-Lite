package fr.isen.m1.gomez.wazeliteski

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.background_main),
                    contentDescription = "background",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.matchParentSize()
                )
                ChoiceView(this@HomeActivity)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxScope.Header(onClick: () -> Unit = {}) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = onClick, modifier = Modifier.size(60.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.waze_logo),
                    contentDescription = "App Icon"
                )
            }
        },
        title = {
            Text(
                text = "Waze Lite Ski",
                color = Color.White,
                fontSize = 40.sp
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(Color(0xFF4EA6CC)),
        modifier = Modifier
            .clip(shape = RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp))
            .align(Alignment.TopCenter)
    )
}

@Composable
fun ChoiceView(activity: HomeActivity) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Header()
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("You are signed in", fontSize = 30.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            MainButton(text = "Sign out") {
                ActivityHelper.signOut(activity)
            }
        }
    }
}