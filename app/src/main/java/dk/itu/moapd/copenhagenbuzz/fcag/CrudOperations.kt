package dk.itu.moapd.copenhagenbuzz.fcag

import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.copenhagenbuzz.fcag.fragments.CreateEventFragment
import dk.itu.moapd.copenhagenbuzz.fcag.models.Event
import io.github.cdimascio.dotenv.dotenv

class CrudOperations {


    companion object {
        private var TAG = "CrudOperations"
    }


    // Environment Variables
    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env"
    }
    private val DATABASE_URL = dotenv["DATABASE_URL"]







    // Add the event to the firebase realtime database and display message upon success or failure
     fun addEventToFirebase(event: Event, view: View) {
        val databaseReference = Firebase.database(DATABASE_URL).reference.child("copenhagen_buzz")

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        event.userId = userId   // Assign user ID to the event

        // Generate a unique key for the event under the 'events' child of 'copenhagen_buzz'
        val key = userId?.let {
            databaseReference.child("events")
                .child(it)
                .push()
                .key
        }


        event.eventId = key // Assign the generated key as the event's ID


        if (key != null) {
            databaseReference.child("events").child(userId).child(key).setValue(event)
                .addOnSuccessListener {
                    Log.d(TAG, "Event added successfully")
                    Snackbar.make(
                        view,
                        "Event added successfully",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to add event", e)
                    Snackbar.make(
                        view,
                        "Failed to add event",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
        }
    }
}