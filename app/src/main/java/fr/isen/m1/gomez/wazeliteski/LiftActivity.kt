package fr.isen.m1.gomez.wazeliteski

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fr.isen.m1.gomez.wazeliteski.data.Lift
import fr.isen.m1.gomez.wazeliteski.data.LiftType
import fr.isen.m1.gomez.wazeliteski.database.DataBaseHelper

class LiftActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            val liftes = remember {
                mutableStateListOf<Lift>()
            }
            Surface(modifier = Modifier.fillMaxSize()) {
                LazyColumn(modifier = Modifier.fillMaxSize()){
                    items(liftes.toList()) {
//                        Text((it.name?:"") + " " + (it.state))
                        LiftView(it)
                    }
                }
                getDBData(liftes)


            }
        }
    }
}
@Composable
fun LiftView(lift: Lift) {
    val type = LiftType.from(lift.type)
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Image(painterResource(type.drawableId()), "")
        Text(lift.name)
        val liftState = remember { mutableStateOf(lift.state) }
        OpenCloseActivityLift(lift, liftState)

    }
}
@Composable
fun getDBData(lifts: SnapshotStateList<Lift>){
    DataBaseHelper.database.getReference("liftes")
        .addListenerForSingleValueEvent(object:ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var index = 0
                val listLiftes = dataSnapshot.children.mapNotNull {
                    var lift = it.getValue(Lift::class.java)
                    lift?.index = index
                    index += 1
                    return@mapNotNull lift
                }
                Log.d("database", lifts.toString())
                lifts.addAll(listLiftes)
                }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("DB", databaseError.toString())

            }
        })
}




//
//@Composable
//fun getDBData(lifts: SnapshotStateList<Lift>){
//    DataBaseHelper.database.getReference("liftes")
//        .addListenerForSingleValueEvent(object:ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val listLiftes = dataSnapshot.children.mapNotNull {
//                    val lift = it.getValue(Lift::class.java)
//                    lift?.key = it.key // Assuming 'key' is a property in your Lift class to hold the Firebase key
//                    lift
//                }
//                Log.d("database", lifts.toString())
//                lifts.addAll(listLiftes)
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                Log.e("DB", databaseError.toString())
//            }
//        })
//}
//
