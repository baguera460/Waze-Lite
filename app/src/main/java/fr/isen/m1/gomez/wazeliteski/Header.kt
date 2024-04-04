package fr.isen.m1.gomez.wazeliteski

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@SuppressLint("RestrictedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(activity: ComponentActivity) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = { ActivityHelper.goToActivity(activity, MenuActivity::class.java, Intent.FLAG_ACTIVITY_CLEAR_TOP) }) {
                Image(
                    painterResource(id = R.drawable.waze_logo),null)
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
            .clip(
                shape = RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp),
            )
    )
}