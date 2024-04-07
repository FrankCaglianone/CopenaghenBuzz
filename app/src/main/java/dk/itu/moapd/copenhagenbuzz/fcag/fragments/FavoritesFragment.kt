package dk.itu.moapd.copenhagenbuzz.fcag.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.copenhagenbuzz.fcag.interfaces.OnItemClickListener
import dk.itu.moapd.copenhagenbuzz.fcag.adapters.FavoriteAdapter
import dk.itu.moapd.copenhagenbuzz.fcag.databinding.FragmentFavoritesBinding
import dk.itu.moapd.copenhagenbuzz.fcag.models.Event
import io.github.cdimascio.dotenv.dotenv


class FavoritesFragment : Fragment(), OnItemClickListener {

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


    private val listener = object : OnItemClickListener {
        override fun onItemClick(position: Int) {
            println("Clicked mf $position")
        }
    }


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
            val query = Firebase.database(DATABASE_URL).reference
                .child("favorites")
                .child(user.uid)
                // .orderByChild("createdAt")

            val options = FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(query, Event::class.java)
                .setLifecycleOwner(this)
                .build()

            // Initialize your FirebaseRecyclerAdapter with the options
            val adapter = FavoriteAdapter(options, listener)

            binding.favoritesRecycleView.layoutManager = LinearLayoutManager(context)
            binding.favoritesRecycleView.adapter = adapter

            // Important: Start listening for database changes
            adapter.startListening()
        }
    }

    override fun onItemClick(position: Int) {
        println("Clicked mf $position")
    }


}