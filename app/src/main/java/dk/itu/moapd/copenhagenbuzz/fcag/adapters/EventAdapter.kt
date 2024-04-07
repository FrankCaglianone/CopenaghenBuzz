package dk.itu.moapd.copenhagenbuzz.fcag.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import dk.itu.moapd.copenhagenbuzz.fcag.models.Event
import dk.itu.moapd.copenhagenbuzz.fcag.R
import dk.itu.moapd.copenhagenbuzz.fcag.databinding.EventRowItemBinding

class EventAdapter(options: FirebaseListOptions<Event>) : FirebaseListAdapter<Event>(options) {

    companion object {
        private var TAG = "EventAdapter.kt"
    }

    private lateinit var row_item_binding : EventRowItemBinding



//    class ViewHolder(val view: View) {
//        val eventName: TextView = view.findViewById(R.id.event_name)
//        val userImage: ImageView = view.findViewById(R.id.user_image)
//        val favouriteButton: ImageButton = view.findViewById(R.id.favourite_button)
//        val eventType: TextView = view.findViewById(R.id.event_type)
//        val eventImage: ImageView = view.findViewById(R.id.event_image)
//        val eventLocation: TextView = view.findViewById(R.id.event_location)
//        val eventDate: TextView = view.findViewById(R.id.event_date)
//        val eventDescription: TextView = view.findViewById(R.id.event_description)
//    }


//    override fun getCount(): Int { return events.size }
//    override fun getItem(position: Int): Event { return events[position] }
//    override fun getItemId(position: Int): Long { return position.toLong() }

//    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
//        // The old view should be reused, if possible.
//        // If convertView == null -> inflate, else reuse the old view
//        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.event_row_item, parent, false)
//
//        //
//        val viewHolder = (view.tag as? ViewHolder) ?: ViewHolder(view)
//
//        // Populate the view holder with the selected `Dummy` data.
//        Log.d(TAG, "Populate an item at position: $position")
//        getItem(position).let { event ->
//            populateViewHolder(viewHolder, event)
//        }
//
//        // Set the new view holder and return the view object.
//        view.tag = viewHolder
//        return view
//    }




//    private fun populateViewHolder(viewHolder: ViewHolder, dummy: Event) {
//        with(viewHolder) {
//            eventName.text = dummy.eventName
//            userImage.setImageResource(R.drawable.baseline_person)
//            favouriteButton.setImageResource(R.drawable.baseline_favorite)
//            eventType.text = dummy.eventType
//            eventImage.setImageResource(R.drawable.ic_launcher_foreground)
//            eventLocation.text = dummy.eventLocation
//            eventDate.text = dummy.eventDate
//            eventDescription.text = dummy.eventDescription
//        }
//
//
//    }

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
            // You could also use Log.d("TAG", "Added to favs") if you want to see the output in Logcat.
        }
    }

}