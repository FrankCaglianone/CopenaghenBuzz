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
import dk.itu.moapd.copenhagenbuzz.fcag.dbOperations.CrudOperations
import dk.itu.moapd.copenhagenbuzz.fcag.data.Event
import dk.itu.moapd.copenhagenbuzz.fcag.R
import io.github.cdimascio.dotenv.dotenv


class EventAdapter(options: FirebaseListOptions<Event>) : FirebaseListAdapter<Event>(options) {

    // A set of private constants used in this class.
    companion object {
        private var TAG = "EventAdapter.kt"
    }


    // Declaring an instance of the CrudOperations class.
    private lateinit var crud: CrudOperations


    // Environment Variables
    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env"
    }
    private val BUCKET_URL = dotenv["STORAGE_URL"]



    /**
     * Populates the details of an event into the specified view layout.
     *
     * This function sets up a view with the details from a provided `Event` object.
     * It sets text fields and images such as event name, type, location, date, description, and
     * user image. It handles image loading for the event from Firebase using the Picasso library
     * if the image URL is available.
     *
     * It also sets up button functionalities for favorite, edit, and delete actions which interact
     * with Firebase based on the event data.
     *
     * @param view The parent view in which event details are displayed.
     * @param dummy The `Event` object containing the event details to populate.
     * @param position The position of the item within the adapter's data set,
     *                 used for handling item-specific operations if necessary.
     */
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
        eventLocation.text = dummy.eventLocation?.address.toString()
        eventDate.text = dummy.eventDate
        eventDescription.text = dummy.eventDescription


        // User id needed to fetch the Photo
        val userId = dummy.userId

        // Get the Photo Url of the event, if not null
        dummy.eventPhotoUrl?.let { photoUrl ->
            if (userId != null) {
                // Navigate into the correct bucket in the Firebase Storage
                Firebase.storage(BUCKET_URL).reference.child("event").child(userId).child(photoUrl).downloadUrl
                    .addOnSuccessListener { url ->
                        // Load the image in eventImage: ImageView using Picasso.
                        Picasso.get().load(url).into(eventImage)
                    }
                    .addOnFailureListener {
                        Log.e("Firebase", "Error getting photo URL", it)
                    }
            }
        }



        // Creating an instance of the CrudOperations class.
        crud = CrudOperations()


        // Setting the click listener for the Add to Favorites Button
        favouriteButton.setOnClickListener {
            // Add the event in the favorites directory in the Firebase Real Time db
            crud.addFavoriteToFirebase(dummy, it)
        }

        // Set the Delete event button listener
        deleteEventListener(view, deleteButton, dummy)

        // Set the Edit event button listener
        editEventListener(view, editButton, dummy)
    }




    /**
     * Sets up a click listener on the given button to delete an event from teh Firebase database.
     *
     * When the button is clicked, it displays an AlertDialog to confirm the deletion of the event.
     * If "Yes" is clicked in the dialog, it triggers the deletion of the event from Firebase using
     * the `CrudOperations` class. The deletion is confirmed or canceled based on the user's response
     * in the dialog.
     *
     * @param view The view that will be used to provide context for the AlertDialog.
     * @param button The button that, when clicked, will trigger the deletion confirmation dialog.
     * @param event The `Event` object that is to be potentially deleted.
     */
    private fun deleteEventListener(view: View, button: Button, event: Event) {
        button.setOnClickListener {
            AlertDialog.Builder(view.context)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Yes") { _, _ ->
                    crud.deleteFromFirebase(event, view)
                }
                .setNegativeButton("No", null)
                .show()
        }
    }






    /**
     * TODO CREATE THE COMMENT
     */
    private fun editEventListener(view: View, button: Button, event: Event) {
        button.setOnClickListener {
            val inflater = LayoutInflater.from(view.context)
            val dialogView = inflater.inflate(R.layout.fragment_create_event, null)

            // Find all views
            val name = dialogView.findViewById<EditText>(R.id.edit_text_event_name)
            val location = dialogView.findViewById<EditText>(R.id.edit_text_event_location)
            val date = dialogView.findViewById<EditText>(R.id.edit_text_event_date)
            val type = dialogView.findViewById<AutoCompleteTextView>(R.id.auto_complete_text_view_event_type)
            val description = dialogView.findViewById<EditText>(R.id.edit_text_event_description)
            val addButton: FloatingActionButton = dialogView.findViewById(R.id.add_event_button)

            // Hide the add_event_button in edit mode
            addButton.visibility = View.GONE

            // Populate the data into the template view using the data object
            name.setText(event.eventName)
            location.setText(event.eventLocation?.address.toString())
            date.setText(event.eventDate)
            type.setText(event.eventType)
            description.setText(event.eventDescription)



            val button = dialogView.findViewById<Button>(R.id.add_image_button)
            button.setOnClickListener {
                AlertDialog.Builder(it.context)
                    .setTitle("Add Image")
                    .setMessage("Would you like to?")
                    .setPositiveButton("Upload a photo") { _, _ ->
//                        if (auth.currentUser != null) {
//                            selectImage()
//                        }
                    }
                    .setNegativeButton("Take a photo") { _, _ ->
//                        dispatchTakePictureIntent()
                    }
                    .show()
            }

            AlertDialog.Builder(view.context)
                .setTitle("Edit Event")
                .setView(dialogView) // Set the inflated layout as view

                .setPositiveButton("Yes") { _, _ ->

                    val newName = name.text.toString().trim()
                    val newLocation = location.text.toString().trim()
                    val newDate = date.text.toString().trim()
                    val newType = type.text.toString().trim()
                    val newDescription = description.text.toString().trim()

                    crud.updateEventInFirebase(event, view, newName, newLocation, newDate, newType, newDescription)
                }
                .setNegativeButton("No", null)
                .show()
        }
    }


}