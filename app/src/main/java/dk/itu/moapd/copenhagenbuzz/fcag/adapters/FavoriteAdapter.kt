package dk.itu.moapd.copenhagenbuzz.fcag.adapters



import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.copenhagenbuzz.fcag.R
import dk.itu.moapd.copenhagenbuzz.fcag.data.Event
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import io.github.cdimascio.dotenv.dotenv


class FavoriteAdapter(options: FirebaseRecyclerOptions<Event>) : FirebaseRecyclerAdapter<Event, FavoriteAdapter.ViewHolder>(options) {

    companion object {
        private var TAG = "FavoriteAdapter.kt"
    }


    // Custom ViewHolder
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        // Enviroment Variables
        private val dotenv = dotenv {
            directory = "/assets"
            filename = "env"
        }
        private val BUCKET_URL = dotenv["STORAGE_URL"]


        private var favoriteName: TextView = view.findViewById(R.id.favorite_event_name)
        private val favoriteType: TextView = view.findViewById(R.id.favorite_event_type)
        private val favoriteImage: ImageView = view.findViewById(R.id.favorite_event_image)
        private val userImage: ImageView = view.findViewById(R.id.favorite_user_image)

        fun bind(event: Event) {
            favoriteName.text = event.eventName
            favoriteType.text = event.eventDescription
//            favoriteImage.setImageResource(R.drawable.ic_launcher_foreground)
            userImage.setImageResource(R.drawable.baseline_person)

            val userId = event.userId

            event.eventPhotoUrl?.let { photoUrl ->
                if (userId != null) {
                    Firebase.storage(BUCKET_URL).reference.child("event").child(userId).child(photoUrl).downloadUrl
                        .addOnSuccessListener { url ->
                            Picasso.get().load(url).into(favoriteImage)
                        }
                        .addOnFailureListener {
                            Log.e("Firebase", "Error getting photo URL", it)

                        }
                }
            }
        }

    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.favorite_row_item, parent, false)
        return ViewHolder(view)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Event) {
        holder.bind(model)
    }



}
