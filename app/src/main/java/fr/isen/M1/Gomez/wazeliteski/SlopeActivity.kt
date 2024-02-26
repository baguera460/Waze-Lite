package fr.isen.M1.Gomez.wazeliteski

import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fr.isen.M1.Gomez.wazeliteski.Data.Slope

class SlopeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slope)
        setContent {
            val slopes = remember {
                mutableStateListOf<Slope>()
            }
            val context = LocalContext.current
            LazyColumn() {

            }
            getDBData(slopes)
        }
    }
}


@Composable
fun getDBData(slopes: SnapshotStateList<Slope>) {
    GetDBData.database.getReference("Slope")
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val listslopes = dataSnapshot.children.mapNotNull { it.getValue(Slope::class.java) }
                Log.d("database", slopes.toString())
                slopes.addAll(listslopes)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("DB", databaseError.toString())
            }
        })
}