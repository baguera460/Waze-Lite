package fr.isen.m1.gomez.wazeliteski

import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(myString : String, color: Color) {
    val context = LocalContext.current
    CenterAlignedTopAppBar(
        title = {
            Text(
                myString, fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(30.dp),
                color = if (color == Color.Black) Color(255, 255, 255) else Color.Black)
        }, colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = color
        ), navigationIcon = {
            IconButton(onClick = {
                val intent = Intent(context, MenuActivity::class.java)
                context.startActivity(intent)
            })
            {
                Icon(
                    imageVector = Icons.Filled.Home, contentDescription = "Home" ,
                    tint = if (color == Color.Black) Color(255, 255, 255) else Color.Black
                )
            }
        }
    )
}