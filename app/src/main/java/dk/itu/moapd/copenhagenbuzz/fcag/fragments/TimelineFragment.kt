package dk.itu.moapd.copenhagenbuzz.fcag.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dk.itu.moapd.copenhagenbuzz.fcag.models.DataViewModel
import dk.itu.moapd.copenhagenbuzz.fcag.adapters.EventAdapter
import dk.itu.moapd.copenhagenbuzz.fcag.databinding.FragmentTimelineBinding


class TimelineFragment : Fragment() {


    private var _binding: FragmentTimelineBinding? = null

    private val binding
        get() = requireNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }



    private val eventListViewModel: DataViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentTimelineBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            eventListViewModel.events.observe(viewLifecycleOwner) {tmp ->
                // Define the list view adapter.
                val adapter = EventAdapter(requireContext(), tmp)
                listView.adapter = adapter
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}