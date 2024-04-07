package dk.itu.moapd.copenhagenbuzz.fcag.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.copenhagenbuzz.fcag.R
import dk.itu.moapd.copenhagenbuzz.fcag.models.Event
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class FavoriteAdapter(options: FirebaseRecyclerOptions<Event>) : FirebaseRecyclerAdapter<Event, FavoriteAdapter.ViewHolder>(options) {

    companion object {
        private var TAG = "FavoriteAdapter.kt"
    }



    // Custom ViewHolder
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var favoriteName: TextView = view.findViewById(R.id.favorite_event_name)
        private val favoriteType: TextView = view.findViewById(R.id.favorite_event_type)
        private val favoriteImage: ImageView = view.findViewById(R.id.favorite_event_image)
        private val userImage: ImageView = view.findViewById(R.id.favorite_user_image)

        fun bind(event: Event) {
            favoriteName.text = event.eventName
            favoriteType.text = event.eventDescription
            favoriteImage.setImageResource(R.drawable.ic_launcher_foreground)
            userImage.setImageResource(R.drawable.baseline_person)
        }
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.favorite_row_item, parent, false)
        return ViewHolder(view)
    }


    // Replace the contents of a view (invoked by the layout manager)
//    override fun onBindViewHolder(holder: FavoriteAdapter.ViewHolder, position: Int) {
//        Log.d(TAG, "Populate an item at position: $position")
//        val favoriteEvent = favoriteEvents[position]
//        holder.bind(favoriteEvent)
//    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Event) {
        holder.bind(model)
    }

    // Return the size of your dataset (invoked by the layout manager)
//    override fun getItemCount() = favoriteEvents.size

}
