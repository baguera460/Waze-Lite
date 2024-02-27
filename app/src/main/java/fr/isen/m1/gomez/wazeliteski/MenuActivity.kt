package fr.isen.m1.gomez.wazeliteski

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ActionMenuView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.components.Component
import fr.isen.m1.gomez.wazeliteski.ui.theme.WazeLiteSkiTheme

enum class LocationType {
    LIFTS, SLOPES;
    @Composable
    fun title(): String {
        return when(this) {
            LIFTS -> stringResource(id = R.string.lifts)
            SLOPES -> stringResource(id = R.string.slopes)
        }
    }

}

interface MenuInterface {
    fun locationPressed(location: LocationType)
}

class MenuActivity : ComponentActivity(),MenuInterface {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //getString(R.string.menu_starter)
            WazeLiteSkiTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SetupView(this)
                }
            }
        }
        Log.d("lifeCycle", "Home Activity - OnCreate")
    }

    override fun locationPressed(location: LocationType) {
        if (location == LocationType.SLOPES){
            val intent = Intent(this, SlopeActivity::class.java)
            startActivity(intent)
        }else{
            val intent = Intent(this, LiftActivity::class.java)
            startActivity(intent)
        }

    }


    override fun onPause() {
        Log.d("lifeCycle", "Home Activity - OnPause")
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        Log.d("lifeCycle", "Home Activity - OnResume")
    }

    override fun onDestroy() {
        Log.d("lifeCycle", "Home Activity - onDestroy")
        super.onDestroy()
    }

}


@Composable
fun CustomButton(type: LocationType, menu: MenuInterface) {
    TextButton(onClick = { menu.locationPressed(type) }) {
        Text(text = type.title(),
            color = Color.Black
        )

    }
}

@Composable
fun SetupView(menu: MenuInterface) {
    WazeLiteSkiTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            //verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Spacer(modifier = Modifier.height(150.dp))
            CustomButton(type = LocationType.LIFTS, menu = menu)
            Spacer(modifier = Modifier.height(10.dp))
            CustomButton(type = LocationType.SLOPES, menu = menu)
        }

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WazeLiteSkiTheme{
        SetupView(MenuActivity())
    }
}
