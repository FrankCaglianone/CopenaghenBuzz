package dk.itu.moapd.copenhagenbuzz.fcag

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import dk.itu.moapd.copenhagenbuzz.fcag.databinding.EventRowItemBinding

class EventAdapter(private val context: Context, private val events: List<Event>) : BaseAdapter() {

    companion object {
        private var TAG = "EventAdapter.kt"
    }


    private lateinit var binding : EventRowItemBinding


    class ViewHolder(val view: View) {
        val eventName: TextView = view.findViewById(R.id.event_name)
        val userImage: ImageView = view.findViewById(R.id.user_image)
        val favouriteButton: ImageButton = view.findViewById(R.id.favourite_button)
        val eventType: TextView = view.findViewById(R.id.event_type)
        val eventImage: ImageView = view.findViewById(R.id.event_image)
        val eventLocation: TextView = view.findViewById(R.id.event_location)
        val eventDate: TextView = view.findViewById(R.id.event_date)
        val eventDescription: TextView = view.findViewById(R.id.event_description)
    }


    override fun getCount(): Int { return events.size }
    override fun getItem(position: Int): Any { return events[position] }
    override fun getItemId(position: Int): Long { return position.toLong() }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // The old view should be reused, if possible.
        // If convertView == null -> inflate, else reuse the old view
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.event_row_item, parent, false)

        val viewHolder = (view.tag as? ViewHolder) ?: ViewHolder(view)

        // Populate the view holder with the selected `Dummy` data.
//        Log.d(TAG, "Populate an item at position: $position")
//        getItem(position)?.let { dummy ->
//            populateViewHolder(viewHolder)
//        }

        // Set the new view holder and return the view object.
        view.tag = viewHolder
        return view
    }




    private fun populateViewHolder(view: View) {

        // Link the UI components
//        val textViewTitle: TextView = view.findViewById(R.id.text_view_title)
//        val textViewSubtitle: TextView = view.findViewById(R.id.text_view_subtitle)
//        val textViewDescription: TextView = view.findViewById(R.id.text_view_description)
//        // Fill out the Material Design card.
//        textViewTitle.text = dummy.cityName
//        textViewSubtitle.text = context.getString(
//            R.string.secondary_text, dummy.country, dummy.zipCode
//        )
//        textViewDescription.text = dummy.description
    }

}