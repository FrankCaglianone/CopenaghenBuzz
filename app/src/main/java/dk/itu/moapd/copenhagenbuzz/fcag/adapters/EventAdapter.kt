package dk.itu.moapd.copenhagenbuzz.fcag.adapters


import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import dk.itu.moapd.copenhagenbuzz.fcag.CrudOperations
import dk.itu.moapd.copenhagenbuzz.fcag.models.Event
import dk.itu.moapd.copenhagenbuzz.fcag.R


class EventAdapter(options: FirebaseListOptions<Event>) : FirebaseListAdapter<Event>(options) {

    companion object {
        private var TAG = "EventAdapter.kt"
    }

    // Declaring an instance of the CrudOperations class.
    private lateinit var crud: CrudOperations


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


        // Creating an instance of the CrudOperations class.
        crud = CrudOperations()


        // Add to favorites button listener
        favouriteButton.setOnClickListener {
            crud.addFavoriteToFirebase(dummy, it)
        }

        // Delete event button listener
        deleteEventListener(view, deleteButton, dummy)
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




}