package dk.itu.moapd.copenhagenbuzz.fcag.adapters


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.copenhagenbuzz.fcag.R
import dk.itu.moapd.copenhagenbuzz.fcag.databinding.FavoriteRowItemBinding
import dk.itu.moapd.copenhagenbuzz.fcag.models.Event

class FavoriteAdapter(private val favoriteEvents: List<Event>) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

    companion object {
        private var TAG = "FavoriteAdapter.kt"
    }



    // Custom ViewHolder
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var binding: FavoriteRowItemBinding

        private val favoriteName: TextView = binding.favoriteEventName
        private val favoriteType: TextView = binding.favoriteEventType
//        private val favoriteImage: ImageView = binding.favoriteEventImage
//        private val userImage: ImageView = binding.favoriteUserImage

        fun bind(event: Event) {
            favoriteName.text = event.eventName
            favoriteType.text = event.eventDescription
//            favoriteImage.imageAlpha = event.
//            userImage.imageAlpha = event.
        }
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.favorite_row_item, parent, false)
        return ViewHolder(view)
    }


    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: FavoriteAdapter.ViewHolder, position: Int) {
        Log.d(TAG, "Populate an item at position: $position")
        val favoriteEvent = favoriteEvents[position]
        holder.bind(favoriteEvent)
    }


    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = favoriteEvents.size


}
