package dk.itu.moapd.copenhagenbuzz.fcag.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseListOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.copenhagenbuzz.fcag.R
import dk.itu.moapd.copenhagenbuzz.fcag.adapters.EventAdapter
import dk.itu.moapd.copenhagenbuzz.fcag.databinding.FragmentTimelineBinding
import dk.itu.moapd.copenhagenbuzz.fcag.models.Event
import io.github.cdimascio.dotenv.dotenv


class TimelineFragment : Fragment() {


    // Bindings
    private var _binding: FragmentTimelineBinding? = null

    private val binding
        get() = requireNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }



    // Enviroment Variables
    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env"
    }
    private val DATABASE_URL = dotenv["DATABASE_URL"]




     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = FragmentTimelineBinding.inflate(inflater, container, false).also {

        _binding = it

        initializeEventList()
    }.root





    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    private fun initializeEventList() {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            val query = Firebase.database(DATABASE_URL).reference
                .child("events")
                .child(user.uid)
                // .orderByChild("createdAt")

            val options = FirebaseListOptions.Builder<Event>()
                .setLayout(R.layout.event_row_item)
                .setQuery(query, Event::class.java)
                .setLifecycleOwner(this)
                .build()

            // Initialize FirebaseRecyclerAdapter with the options
            val adapter = EventAdapter(options)

            binding.listView.adapter = adapter

            // Important: Start listening for database changes
            adapter.startListening()
        }
    }


}