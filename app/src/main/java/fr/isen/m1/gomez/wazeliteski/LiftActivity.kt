package fr.isen.m1.gomez.wazeliteski

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fr.isen.m1.gomez.wazeliteski.data.Lift
import fr.isen.m1.gomez.wazeliteski.database.DataBaseHelper

class LiftActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lift)
        setContent{
            val liftes = remember {
                mutableStateListOf<Lift>()
            }
            Surface(modifier = Modifier.fillMaxSize()) {
                LazyColumn(modifier = Modifier.fillMaxSize()){
                    items(liftes.toList()) {
                        Text((it.type.toString()) + " " + (it.name?:"") + " " + (it.state))

                    }
                }
                getDBData(liftes)

            }
        }
    }
}

@Composable
fun getDBData(lifts: SnapshotStateList<Lift>){
    DataBaseHelper.database.getReference("liftes")
        .addListenerForSingleValueEvent(object:ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val listLiftes = dataSnapshot.children.mapNotNull { it.getValue(Lift::class.java)}
                Log.d("database", lifts.toString())
                lifts.addAll(listLiftes)
                }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("DB", databaseError.toString())

            }
        })
}
