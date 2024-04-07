package dk.itu.moapd.copenhagenbuzz.fcag.adapters


import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import dk.itu.moapd.copenhagenbuzz.fcag.models.Event
import dk.itu.moapd.copenhagenbuzz.fcag.R


class EventAdapter(options: FirebaseListOptions<Event>) : FirebaseListAdapter<Event>(options) {

    companion object {
        private var TAG = "EventAdapter.kt"
    }


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


        // Populate the data into the template view using the data object
        eventName.text = dummy.eventName
        userImage.setImageResource(R.drawable.baseline_person)
        favouriteButton.setImageResource(R.drawable.baseline_favorite)
        eventType.text = dummy.eventType
        eventImage.setImageResource(R.drawable.ic_launcher_foreground)
        eventLocation.text = dummy.eventLocation
        eventDate.text = dummy.eventDate
        eventDescription.text = dummy.eventDescription

        favoriteEventListener(favouriteButton)
    }


    private fun favoriteEventListener(favoriteButton: ImageButton) {
        favoriteButton.setOnClickListener {
            println("Added to favs")
        }
    }

}