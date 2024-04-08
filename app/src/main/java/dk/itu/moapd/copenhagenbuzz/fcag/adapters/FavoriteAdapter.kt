package dk.itu.moapd.copenhagenbuzz.fcag.adapters


import android.util.Log
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.copenhagenbuzz.fcag.interfaces.OnItemClickListener
import io.github.cdimascio.dotenv.dotenv

class FavoriteAdapter(options: FirebaseRecyclerOptions<Event>, private val listener: OnItemClickListener) : FirebaseRecyclerAdapter<Event, FavoriteAdapter.ViewHolder>(options) {

    companion object {
        private var TAG = "FavoriteAdapter.kt"
    }

    // Enviroment Variables
    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env"
    }
    private val DATABASE_URL = dotenv["DATABASE_URL"]


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



    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Event) {
        holder.bind(model)
        holder.itemView.setOnClickListener {
            listener.onItemClick(position)
        }
    }




     fun deleteItem(position: Int) {
        // Get the Firebase database reference key for the item at the specified position
        val key = getRef(position).key

        key?.let {
            FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
                Firebase.database(DATABASE_URL).reference
                    .child("favorites")
                    .child(userId)
                    .child(it)
                    .removeValue()
                    .addOnSuccessListener {
                        Log.d(TAG, "Event successfully deleted.")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Failed to delete event.", e)
                    }
            }
        }
    }





}
