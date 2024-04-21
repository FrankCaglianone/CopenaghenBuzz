package dk.itu.moapd.copenhagenbuzz.fcag.adapters


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import dk.itu.moapd.copenhagenbuzz.fcag.CrudOperations
import dk.itu.moapd.copenhagenbuzz.fcag.data.Event
import dk.itu.moapd.copenhagenbuzz.fcag.R
import io.github.cdimascio.dotenv.dotenv


class EventAdapter(options: FirebaseListOptions<Event>) : FirebaseListAdapter<Event>(options) {

    companion object {
        private var TAG = "EventAdapter.kt"
    }

    // Declaring an instance of the CrudOperations class.
    private lateinit var crud: CrudOperations


    // Enviroment Variables
    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env"
    }
    private val BUCKET_URL = dotenv["STORAGE_URL"]


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
//        eventImage.setImageResource(R.drawable.ic_launcher_foreground)
        eventLocation.text = dummy.eventLocation?.address.toString()
        eventDate.text = dummy.eventDate
        eventDescription.text = dummy.eventDescription


        // Getting photo from Firebase Storage
        val userId = dummy.userId
        dummy.eventPhotoUrl?.let { photoUrl ->
            if (userId != null) {
                Firebase.storage(BUCKET_URL).reference.child("event").child(userId).child(photoUrl).downloadUrl
                    .addOnSuccessListener { url ->
                        Picasso.get().load(url).into(eventImage)
                    }
                    .addOnFailureListener {
                        Log.e("Firebase", "Error getting photo URL", it)

                    }
            }
        }







        // Creating an instance of the CrudOperations class.
        crud = CrudOperations()


        // Add to favorites button listener
        favouriteButton.setOnClickListener {
            crud.addFavoriteToFirebase(dummy, it)
        }

        // Delete event button listener
        deleteEventListener(view, deleteButton, dummy)

        // Edit event listener
        editEventListener(view, editButton, dummy)
    }






    private fun deleteEventListener(view: View, button: Button, event: Event) {
        button.setOnClickListener {
            AlertDialog.Builder(view.context)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Yes") { dialog, which ->
                    crud.deleteFromFirebase(event, view)
                }
                .setNegativeButton("No", null)
                .show()
        }
    }


    private fun editEventListener(view: View, button: Button, event: Event) {
        button.setOnClickListener {
            val inflater = LayoutInflater.from(view.context)
            val dialogView = inflater.inflate(R.layout.fragment_create_event, null)

            // Hide the add_event_button in edit mode
            dialogView.findViewById<FloatingActionButton>(R.id.add_event_button).visibility = View.GONE

            AlertDialog.Builder(view.context)
                .setTitle("Edit Event")
                .setView(dialogView) // Set the inflated layout as view

                .setPositiveButton("Yes") { dialog, which ->

                    var name = dialogView.findViewById<EditText>(R.id.edit_text_event_name).text.toString().trim()
                    var location = dialogView.findViewById<EditText>(R.id.edit_text_event_location).text.toString().trim()
                    var date = dialogView.findViewById<EditText>(R.id.edit_text_event_date).text.toString().trim()
                    var type = dialogView.findViewById<AutoCompleteTextView>(R.id.auto_complete_text_view_event_type).text.toString().trim()
                    var description = dialogView.findViewById<EditText>(R.id.edit_text_event_description).text.toString().trim()

                    when {
                        name.isEmpty() -> name = event.eventName.toString()
                        location.isEmpty() -> location = event.eventLocation?.address.toString()
                        date.isEmpty() -> date = event.eventDate.toString()
                        type.isEmpty() -> type = event.eventType.toString()
                        description.isEmpty() -> description = event.eventDescription.toString()
                    }

                    crud.updateEventInFirebase(event, view, name, location, date, type, description)
                }
                .setNegativeButton("No", null)
                .show()
        }
    }


}