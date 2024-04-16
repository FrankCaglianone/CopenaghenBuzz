package dk.itu.moapd.copenhagenbuzz.fcag

import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.copenhagenbuzz.fcag.data.Event
import dk.itu.moapd.copenhagenbuzz.fcag.data.EventLocation
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






    fun addFavoriteToFirebase(event: Event, view: View) {
        val databaseReference = Firebase.database(DATABASE_URL).reference.child("copenhagen_buzz")
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val key = event.eventId

        userId?.let {
            databaseReference.child("favorites")
                .child(it)
                .push()

        } // Generate a unique key for the event


        if (key != null) {
            if (userId != null) {
                databaseReference.child("favorites").child(userId).child(key).setValue(event)
                    .addOnSuccessListener { tmp ->
                        Log.d(TAG, "Added to favorites")
                        Snackbar.make(
                            view,
                            "Added to favorites",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Failed to add to favorites", e)
                        Snackbar.make(
                            view,
                            "Failed to add to favorites",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }






    fun deleteFromFirebase(event: Event, view: View) {
        val key = event.eventId
        var deleted_from_events = false

        key?.let {
            FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
                // Delete from events
                Firebase.database(DATABASE_URL).reference.child("copenhagen_buzz")
                    .child("events")
                    .child(userId)
                    .child(it)
                    .removeValue()
                    .addOnSuccessListener {
                        Log.d(TAG, "Successfully deleted from events")
                        deleted_from_events = true
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Failed to delete from events", e)
                    }

                // Delete from favorites
                Firebase.database(DATABASE_URL).reference.child("copenhagen_buzz")
                    .child("favorites")
                    .child(userId)
                    .child(it)
                    .removeValue()
                    .addOnSuccessListener {tmp ->
                        if (deleted_from_events) {
                            Log.d(TAG, "Event successfully deleted")
                            Snackbar.make(view, "Event successfully deleted", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Failed to delete event.", e)
                    }


            }
        }
    }





     fun updateEventInFirebase(event: Event, view: View, newName: String, newLocation: String, newDate: String, newType: String, newDescription: String,) {
         val key = event.eventId
         var updated = false

         var location = EventLocation()
         val locationString = event.eventLocation?.address.toString()
         if(locationString == newLocation) {
             location = event.eventLocation!!
         } else {
//             location = Calculate(newLocation)
             location.latitude = 0.0
             location.longitude = 0.0
             location.address = "Changed"
         }


         key?.let {
             FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->

                 // Creating a map to hold the updated values
                 val updateValues = mapOf(
                     "eventName" to newName,
                     "eventLocation" to location,
                     "eventDate" to newDate,
                     "eventType" to newType,
                     "eventDescription" to newDescription
                 )


                 Firebase.database(DATABASE_URL).reference.child("copenhagen_buzz")
                     .child("events")
                     .child(userId)
                     .child(it)
                     .updateChildren(updateValues)

                     .addOnSuccessListener {
                         updated = true
                     }
                     .addOnFailureListener {
                         Snackbar.make(view, "Failed to update event", Snackbar.LENGTH_SHORT).show()
                     }


//                 Firebase.database(DATABASE_URL).reference.child("copenhagen_buzz")
//                     .child("favorites")
//                     .child(userId)
//                     .child(it)
//                     .updateChildren(updateValues)
//
//                     .addOnSuccessListener {
//                         if(updated) {
//                             Snackbar.make(view, "Event updated successfully", Snackbar.LENGTH_SHORT).show()
//                         }
//                     }
//                     .addOnFailureListener {
//                         Snackbar.make(view, "Failed to update event", Snackbar.LENGTH_SHORT).show()
//                     }
             }
         }
     }


}