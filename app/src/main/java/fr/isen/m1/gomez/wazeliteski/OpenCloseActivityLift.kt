package fr.isen.m1.gomez.wazeliteski

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import fr.isen.m1.gomez.wazeliteski.data.Lift
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text

import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fr.isen.m1.gomez.wazeliteski.database.DataBaseHelper
//
//@Composable
//fun OpenCloseActivityLift(lift: Lift) {
//    val backgroundColor = if (lift.state) { Color.Green } else { Color.Red }
//    Box(
//        modifier = Modifier
//            .border(1.dp, colorResource(id = R.color.black), RoundedCornerShape(CornerSize(8.dp)))
//            .clip(RoundedCornerShape(CornerSize(8.dp)))
//            .background(backgroundColor)
//            .clickable {
//                val newValue = !lift.state
//                Firebase.database.reference.child("liftes/${lift.index}/state").setValue(newValue)
//                lift.state = newValue
//                updateLiftStateInFirebase(lift)
//            }
//    ) {
//        Row(Modifier.padding(8.dp)) {
//            Text(if (lift.state) { "Ouverte" } else { "Fermée" })
//        }
//    }
//}

@Composable
fun OpenCloseActivityLift(lift: Lift, liftState: MutableState<Boolean>) {
    // Using the state passed in to determine background color
    val backgroundColor = if (liftState.value) Color.Green else Color.Red

    Box(
        modifier = Modifier
            .border(1.dp, colorResource(id = R.color.black), RoundedCornerShape(CornerSize(8.dp)))
            .clip(RoundedCornerShape(CornerSize(8.dp)))
            .background(backgroundColor)
            .clickable {
                val newValue = !liftState.value
                // Update the state locally
                liftState.value = newValue
                // Also, trigger the update in Firebase
                updateLiftStateInFirebase(lift,newValue)
            }
            .padding(8.dp)
    ) {
        Row {
            Text(text = if (liftState.value) "Ouverte" else "Fermée")
        }
    }
}

fun updateLiftStateInFirebase(lift: Lift, newState: Boolean) {
    val liftId = lift.index.toString()
    DataBaseHelper.database.getReference("liftes").child(liftId).child("state").setValue(newState)
        .addOnSuccessListener {
            Log.d("Firebase", "Lift state updated successfully")
        }
        .addOnFailureListener { e ->
            Log.e("Firebase", "Error updating lift state", e)
        }
}
