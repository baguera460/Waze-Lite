package fr.isen.m1.gomez.wazeliteski

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.m1.gomez.wazeliteski.ui.theme.WazeLiteSkiTheme

class MainActivity : ComponentActivity() {/*
    val database = FirebaseDatabase.getInstance("https://waze-lite-ski-default-rtdb.europe-west1.firebasedatabase.app/")
    var myRef = database.getReference("Slope")

    var pisteData by remember { mutableStateOf("aucune donnée") }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WazeLiteSkiTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Greeting()
                    val context = LocalContext.current
                    val intent = Intent(context, SlopeActivity::class.java)
                    context.startActivity(intent)
                }
            }

        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    Column {
        Text(text = "Hello")
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Test")
    }

}

@Composable
fun Input(
    value: String,
    text: String,
    type: KeyboardType,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onValueChange: (String) -> Unit
) {
    var visualTransformationRemember by remember { mutableStateOf(visualTransformation) }
    var eyeIconId by remember { mutableIntStateOf(R.drawable.eye_closed) }

    Column {
        Text(
            text = text,
            fontSize = 20.sp,
            color = Color(0xFF000000),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start,
            modifier = Modifier.width(TextFieldDefaults.MinWidth)
        )
        TextField(value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = type, autoCorrect = false
            ),
            visualTransformation = visualTransformationRemember,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFFFFFFF),
                unfocusedContainerColor = Color(0xFFFFFFFF),
                focusedTextColor = Color(0xFF000000),
                unfocusedTextColor = Color(0xFF000000),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTrailingIconColor = Color(0xFF000000),
                unfocusedTrailingIconColor = Color(0xFF000000)
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.border(2.dp, Color(0xFF93B1FF), RoundedCornerShape(20.dp)),
            textStyle = TextStyle(fontSize = 17.sp),
            trailingIcon = {
                if (value.isNotEmpty()) {
                    Row {
                        if (text.contains("Password", true)) {
                            IconButton(onClick = {
                                if (eyeIconId == R.drawable.eye_opened) {
                                    visualTransformationRemember =
                                        PasswordVisualTransformation(mask = '•')
                                    eyeIconId = R.drawable.eye_closed
                                } else {
                                    visualTransformationRemember = VisualTransformation.None
                                    eyeIconId = R.drawable.eye_opened
                                }
                            }) {
                                Icon(
                                    painter = painterResource(id = eyeIconId),
                                    contentDescription = "eye icon",
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                        IconButton(onClick = { onValueChange("") }) {
                            Icon(
                                imageVector = Icons.Filled.Clear, contentDescription = "Clear"
                            )
                        }
                    }
                }
            })
    }
}


