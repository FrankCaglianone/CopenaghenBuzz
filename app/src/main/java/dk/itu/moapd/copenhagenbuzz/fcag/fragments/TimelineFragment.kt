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
import dk.itu.moapd.copenhagenbuzz.fcag.data.Event
import io.github.cdimascio.dotenv.dotenv


class TimelineFragment : Fragment() {

    // Binding
    private var _binding: FragmentTimelineBinding? = null
    private val binding
        get() = requireNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }



    // Environment Variables
    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env"
    }
    private val DATABASE_URL = dotenv["DATABASE_URL"]



    // Called when the fragment is being created.
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     }


    // Called to create the layout of the fragment.
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = FragmentTimelineBinding.inflate(inflater, container, false).also {

        _binding = it

        // Fetch all the events of the logged in user from the DB and display them
        initializeEventList()
    }.root





    // Called after the view created by onCreateView() has been created and ensures that the view hierarchy is fully initialized.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    // Called when the view previously created by onCreateView() has been detached from the fragment.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }





    /**
     * This function fetches all the events of the logged in user from the Firebase db and displays
     * them in the fragment.
     * Note: The layout used "event_row_item" is inflated here in the options since we are using a
     * List View and not a Recycler View.
     */
    /**
     * Initializes and configures the list adapter to display events associated with the currently
     * logged-in user from Firebase.
     * This function constructs a Firebase query to retrieve events, sets up a Firebase list adapter
     * using custom options, and binds this adapter to a ListView.
     *
     * The layout for individual rows in the list is defined by "event_row_item".
     * It also starts listening for real-time updates to the events, reflecting any changes immediately.
     */
    private fun initializeEventList() {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            // Create the query to fetch the users events
            val query = Firebase.database(DATABASE_URL).reference.child("copenhagen_buzz")
                .child("events")
                .child(user.uid)


            // Create the options to pass teh adapter
            val options = FirebaseListOptions.Builder<Event>()
                .setLayout(R.layout.event_row_item)
                .setQuery(query, Event::class.java)
                .setLifecycleOwner(this)
                .build()

            // Initialize FirebaseRecyclerAdapter with the options
            val adapter = EventAdapter(options)

            // Set the adapter
            binding.listView.adapter = adapter

            // Important: Start listening for database changes
            adapter.startListening()
        }
    }


}