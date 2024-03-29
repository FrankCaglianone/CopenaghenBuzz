package dk.itu.moapd.copenhagenbuzz.fcag.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dk.itu.moapd.copenhagenbuzz.fcag.adapters.FavoriteAdapter
import dk.itu.moapd.copenhagenbuzz.fcag.databinding.FragmentFavoritesBinding
import dk.itu.moapd.copenhagenbuzz.fcag.models.DataViewModel


class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null

    private val binding
        get() = requireNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }


    private val favoritesListViewModel: DataViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentFavoritesBinding.inflate(inflater, container, false).also {
        _binding = it
        binding.favoritesRecycleView.layoutManager = LinearLayoutManager(context)

        favoritesListViewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            // favorites is now List<Event> and not LiveData
            val adapter = FavoriteAdapter(favorites)
            binding.favoritesRecycleView.adapter = adapter
        }
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply { }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



//    dummy
}