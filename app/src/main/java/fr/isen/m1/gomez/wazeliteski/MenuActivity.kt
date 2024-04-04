//package fr.isen.m1.gomez.wazeliteski
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.widget.ActionMenuView
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.animation.VectorConverter
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Divider
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.font.FontStyle
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextDecoration
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.google.firebase.Firebase
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.components.Component
//import fr.isen.m1.gomez.wazeliteski.ui.theme.WazeLiteSkiTheme
//
//enum class LocationType {
//    LIFTS, SLOPES;
//    @Composable
//    fun title(): String {
//        return when(this) {
//            LIFTS -> stringResource(id = R.string.lifts)
//            SLOPES -> stringResource(id = R.string.slopes)
//        }
//    }
//
//}
//
//interface MenuInterface {
//    fun locationPressed(location: LocationType)
//}
//
//class MenuActivity : ComponentActivity(),MenuInterface {
//    private lateinit var auth: FirebaseAuth
//
//    companion object{
//        const val LOGIN = "SETUP_EXTRA_KEY"
//    }
//    override fun onCreate(savedInstanceState: Bundle?) {
//
//        //val user = Firebase.auth.currentUser
//        super.onCreate(savedInstanceState)
//
//
//        setContent {
//            //getString(R.string.menu_starter)
//            WazeLiteSkiTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    //SetupView(this, user!!)
//                    SetupView(this)
//                }
//            }
//        }
//        Log.d("lifeCycle", "Home Activity - OnCreate")
//    }
//
//    override fun locationPressed(location: LocationType) {
//        if (location == LocationType.SLOPES){
//            val intent = Intent(this, SlopeActivity::class.java)
//            startActivity(intent)
//        }else{
//            val intent = Intent(this, LiftActivity::class.java)
//            startActivity(intent)
//        }
//
//    }
//
//
//    override fun onPause() {
//        Log.d("lifeCycle", "Home Activity - OnPause")
//        super.onPause()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        Log.d("lifeCycle", "Home Activity - OnResume")
//    }
//
//    override fun onDestroy() {
//        Log.d("lifeCycle", "Home Activity - onDestroy")
//        super.onDestroy()
//    }
//
//}
//
//
//@Composable
//fun CustomButton(type: LocationType, menu: MenuInterface) {
//    TextButton(onClick = { menu.locationPressed(type) }) {
//        Text(text = type.title(),
//            color = Color.Black
//        )
//
//    }
//}
//
//@Composable
//fun SetupView(menu: MenuInterface) {
//
//    WazeLiteSkiTheme {
//        Surface(
//            modifier = Modifier.fillMaxSize(),
//            color = MaterialTheme.colorScheme.background
//        ) {
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
//                modifier = Modifier.padding(25.dp)
//            ) {
//                Image(painterResource(id = R.drawable.waze_logo),null)
//                Text(
//                    text = stringResource( R.string.app_name),
//                    color = Color.LightGray,
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.Bold
//                )
//                Spacer(modifier = Modifier.height(150.dp))
//                CustomButton(type = LocationType.LIFTS, menu = menu)
//                Spacer(modifier = Modifier.height(10.dp))
//                CustomButton(type = LocationType.SLOPES, menu = menu)
//                TextButton(onClick = {})
//                {
//                    Text(text = "Déconnection", color = Color(0,0,125), textDecoration = TextDecoration.Underline, fontStyle = FontStyle.Italic,
//                        fontSize = 10.sp)
//                }
//            }
//
//        }
//    }
//}
//
////TODO quand @Dorian aura fini, on fait le nouvea setupview avec l'utilisateur dedans
///*@Composable
//fun SetupView(menu: MenuInterface, user : FirebaseUser) {
//
//    WazeLiteSkiTheme {
//        Surface(
//            modifier = Modifier.fillMaxSize(),
//            color = MaterialTheme.colorScheme.background
//        ) {
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
//                modifier = Modifier.padding(25.dp)
//            ) {
//                Image(painterResource(id = R.drawable.mountain_sunrise_icon),null)
//                Text(
//                    text = stringResource( R.string.app_name),
//                    color = Color.LightGray,
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.Bold
//                )
//                Spacer(modifier = Modifier.height(150.dp))
//                Text(text = )
//                CustomButton(type = LocationType.LIFTS, menu = menu)
//                Spacer(modifier = Modifier.height(10.dp))
//                CustomButton(type = LocationType.SLOPES, menu = menu)
//                TextButton(onClick = {})
//                {
//                    Text(text = "Déconnection", color = Color(0,0,125), textDecoration = TextDecoration.Underline, fontStyle = FontStyle.Italic,
//                        fontSize = 10.sp)
//                }
//            }
//
//        }
//    }
//}*/
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    WazeLiteSkiTheme{
//        SetupView(MenuActivity())
//    }
//}



package fr.isen.m1.gomez.wazeliteski

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ActionMenuView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.VectorConverter
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
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
    private lateinit var auth: FirebaseAuth

    companion object{
        const val LOGIN = "SETUP_EXTRA_KEY"
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        //val user = Firebase.auth.currentUser
        super.onCreate(savedInstanceState)


        setContent {
            //getString(R.string.menu_starter)
            WazeLiteSkiTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //SetupView(this, user!!)
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
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(25.dp)
            ) {
                Image(painterResource(id = R.drawable.waze_logo),null)
                Text(
                    text = stringResource( R.string.app_name),
                    color = Color.LightGray,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(150.dp))
                CustomButton(type = LocationType.LIFTS, menu = menu)
                Spacer(modifier = Modifier.height(10.dp))
                CustomButton(type = LocationType.SLOPES, menu = menu)
                TextButton(onClick = {})
                {


                    Text(text = "Déconnexion", color = Color(0,0,125), textDecoration = TextDecoration.Underline, fontStyle = FontStyle.Italic,
                                fontSize = 10.sp)


                }
            }

        }
    }
}

//TODO quand @Dorian aura fini, on fait le nouvea setupview avec l'utilisateur dedans
/*@Composable
fun SetupView(menu: MenuInterface, user : FirebaseUser) {

    WazeLiteSkiTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(25.dp)
            ) {
                Image(painterResource(id = R.drawable.mountain_sunrise_icon),null)
                Text(
                    text = stringResource( R.string.app_name),
                    color = Color.LightGray,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(150.dp))
                Text(text = )
                CustomButton(type = LocationType.LIFTS, menu = menu)
                Spacer(modifier = Modifier.height(10.dp))
                CustomButton(type = LocationType.SLOPES, menu = menu)
                TextButton(onClick = {})
                {
                    Text(text = "Déconnection", color = Color(0,0,125), textDecoration = TextDecoration.Underline, fontStyle = FontStyle.Italic,
                        fontSize = 10.sp)
                }
            }

        }
    }
}*/

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WazeLiteSkiTheme{
        SetupView(MenuActivity())
    }
}