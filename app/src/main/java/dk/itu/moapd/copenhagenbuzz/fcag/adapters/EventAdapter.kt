package dk.itu.moapd.copenhagenbuzz.fcag.adapters


import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.copenhagenbuzz.fcag.models.Event
import dk.itu.moapd.copenhagenbuzz.fcag.R
import dk.itu.moapd.copenhagenbuzz.fcag.fragments.CreateEventFragment
import io.github.cdimascio.dotenv.dotenv


class EventAdapter(options: FirebaseListOptions<Event>) : FirebaseListAdapter<Event>(options) {

    companion object {
        private var TAG = "EventAdapter.kt"
    }


    // Environment Variables
    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env"
    }
    private val DATABASE_URL = dotenv["DATABASE_URL"]


    override fun populateView(view: View, dummy: Event, position: Int) {
        // Find the views to populate in inflated template
        val eventName: TextView = view.findViewById(R.id.event_name)
        val userImage: ImageView = view.findViewById(R.id.user_image)
        val favouriteButton: ImageButton = view.findViewById(R.id.favourite_button)
        val eventType: TextView = view.findViewById(R.id.event_type)
        val eventImage: ImageView = view.findViewById(R.id.event_image)
        val eventLocation: TextView = view.findViewById(R.id.event_location)
        val eventDate: TextView = view.findViewById(R.id.event_date)
        val eventDescription: TextView = view.findViewById(R.id.event_description)
        val editButton: Button = view.findViewById(R.id.edit_button)
        val deleteButton: Button = view.findViewById(R.id.delete_button)



        // Populate the data into the template view using the data object
        eventName.text = dummy.eventName
        userImage.setImageResource(R.drawable.baseline_person)
        favouriteButton.setImageResource(R.drawable.baseline_favorite)
        eventType.text = dummy.eventType
        eventImage.setImageResource(R.drawable.ic_launcher_foreground)
        eventLocation.text = dummy.eventLocation
        eventDate.text = dummy.eventDate
        eventDescription.text = dummy.eventDescription


        favoriteEventListener(favouriteButton, dummy)
        deleteEventListener(view, deleteButton, dummy)
    }



    private fun favoriteEventListener(favoriteButton: ImageButton, event: Event) {
        favoriteButton.setOnClickListener {

            val databaseReference = Firebase.database(DATABASE_URL).reference
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val key = event.eventId

            userId?.let {
                databaseReference.child("favorites")
                    .child(it)
                    .push()

            } // Generate a unique key for the event
//            event.eventId = key // Assign the generated key as the event's ID



            if (key != null) {
                if (userId != null) {
                    databaseReference.child("favorites").child(userId).child(key).setValue(event)
                        .addOnSuccessListener { tmp ->
                            Log.d(TAG, "Added to favorites")
                            Snackbar.make(
                                it,
                                "Added to favorites",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Failed to add to favorites", e)
                            Snackbar.make(
                                it,
                                "Failed to add to favorites",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                }
            }
        }
    }




    private fun deleteEventListener(view: View, button: Button, event: Event) {
        button.setOnClickListener {
            AlertDialog.Builder(view.context)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Yes") { dialog, which ->
                    deleteEvent(event, it)
                }
                .setNegativeButton("No", null)
                .show()
        }
    }




    private fun deleteEvent(event: Event, view: View) {
        val key = event.eventId
        var deleted_from_events = false

        key?.let {
            FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
                // Delete from events
                Firebase.database(DATABASE_URL).reference
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
                Firebase.database(DATABASE_URL).reference
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



}