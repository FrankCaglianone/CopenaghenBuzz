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
import dk.itu.moapd.copenhagenbuzz.fcag.SwipeToDeleteCallback
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
    ): View = FragmentFavoritesBinding.inflate(inflater, container, false).also {
        _binding = it

        binding.favoritesRecycleView.layoutManager = LinearLayoutManager(context)

        initializeFavouritesList()
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    private fun initializeFavouritesList() {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            val query = Firebase.database(DATABASE_URL).reference.child("copenhagen_buzz")
                .child("favorites")
                .child(user.uid)
                // .orderByChild("createdAt")

            val options = FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(query, Event::class.java)
                .setLifecycleOwner(this)
                .build()

            // Initialize your FirebaseRecyclerAdapter with the options
            val adapter = FavoriteAdapter(options)

            binding.favoritesRecycleView.layoutManager = LinearLayoutManager(context)
            binding.favoritesRecycleView.adapter = adapter


            // Setup the RecyclerView.
            setupRecyclerView(adapter)

            // Important: Start listening for database changes
            adapter.startListening()
        }
    }






    private fun setupRecyclerView(adapter: FavoriteAdapter) {
        binding.favoritesRecycleView.apply {

            // Set the layout manager for the RecyclerView to be a LinearLayoutManager.
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = null
            addItemDecoration(
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            )

            // Set the adapter for the RecyclerView to be the CustomAdapter.
            this.adapter = adapter

            // Adding the swipe option.
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