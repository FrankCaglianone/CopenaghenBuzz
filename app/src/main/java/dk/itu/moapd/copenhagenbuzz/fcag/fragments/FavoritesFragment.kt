package dk.itu.moapd.copenhagenbuzz.fcag.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.copenhagenbuzz.fcag.dbOperations.SwipeToDeleteCallback
import dk.itu.moapd.copenhagenbuzz.fcag.adapters.FavoriteAdapter
import dk.itu.moapd.copenhagenbuzz.fcag.databinding.FragmentFavoritesBinding
import dk.itu.moapd.copenhagenbuzz.fcag.data.Event
import io.github.cdimascio.dotenv.dotenv


class FavoritesFragment : Fragment() {

    // Binding
    private var _binding: FragmentFavoritesBinding? = null

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
    ): View = FragmentFavoritesBinding.inflate(inflater, container, false).also {
        _binding = it

        // Set the layout manager
        binding.favoritesRecycleView.layoutManager = LinearLayoutManager(context)

        // Fetch all the favorite events of the logged in user from the DB and display them
        initializeFavouritesList()
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
     * Initializes and configures the RecyclerView adapter to display the favorite events of the
     * currently logged-in user from Firebase.
     * It constructs a Firebase query to retrieve favorite events, sets up a FirebaseRecyclerAdapter
     * using specified options, and binds this adapter to a RecyclerView.
     * * It also starts listening for real-time updates to the favorites, reflecting any changes immediately.
     *
     * Notes: The individual row layout is specified by "favorite_row_item" and is inflated in the
     * adapter's onCreateViewHolder method of the "FavoriteAdapter".
     */
    private fun initializeFavouritesList() {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            // Create the query to fetch the users events
            val query = Firebase.database(DATABASE_URL).reference.child("copenhagen_buzz")
                .child("favorites")
                .child(user.uid)


            // Create the options to pass teh adapter
            val options = FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(query, Event::class.java)
                .setLifecycleOwner(this)
                .build()


            // Initialize your FirebaseRecyclerAdapter "FavoriteAdapter" with the options
            val adapter = FavoriteAdapter(options)

            // Set the adapter
            binding.favoritesRecycleView.layoutManager = LinearLayoutManager(context)
            binding.favoritesRecycleView.adapter = adapter

            // Setup the RecyclerView.
            setupRecyclerView(adapter)

            // Important: Start listening for database changes
            adapter.startListening()
        }
    }








    /**
     * Sets up a RecyclerView with a LinearLayoutManager, disables default item animations,
     * adds dividers between items, sets the provided adapter, and adds swipe-to-delete
     * functionality using an ItemTouchHelper with a custom SwipeToDeleteCallback.
     *
     * @param adapter The adapter to be set for the RecyclerView.
     */
    private fun setupRecyclerView(adapter: FavoriteAdapter) {
        binding.favoritesRecycleView.apply {

            // Set the layout manager for the RecyclerView to be a LinearLayoutManager.
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = null
            addItemDecoration(
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            )

            // Set the adapter for the RecyclerView to be the FavoriteAdapter.
            this.adapter = adapter

            // Add the Swipe to delete callback option
            val swipeHandler = object : SwipeToDeleteCallback() {
                override  fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    super.onSwiped(viewHolder, direction)
                    adapter.getRef(viewHolder.absoluteAdapterPosition).removeValue()
                }
            }
            ItemTouchHelper(swipeHandler).attachToRecyclerView(this)
        }
    }


}