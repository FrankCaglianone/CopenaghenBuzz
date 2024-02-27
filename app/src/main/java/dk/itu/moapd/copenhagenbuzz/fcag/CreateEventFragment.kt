package dk.itu.moapd.copenhagenbuzz.fcag

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.copenhagenbuzz.fcag.databinding.FragmentCreateEventBinding


class CreateEventFragment : Fragment() {


    private var _binding: FragmentCreateEventBinding? = null

    private val binding
        get() = requireNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }


    companion object {
        private var TAG = "CreateEventFragment"
    }

    // An instance of the ‘Event' class.
    private val event : Event = Event("", "", "", "", "")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentCreateEventBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createEventListener()
        binding.apply { }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }








        private fun createEventListener() {

        with(binding) {
            binding.addEventButton.setOnClickListener {
                if (binding.editTextEventLocation.text.toString().isNotEmpty() && binding.editTextEventName.text.toString()
                        .isNotEmpty()
                ) {

                    // Getting all variables inputs
                    event.eventName = binding.editTextEventName.text.toString().trim()
                    event.eventLocation = binding.editTextEventLocation.text.toString().trim()
                    event.eventDate = binding.editTextEventDate.text.toString().trim()
                    event.eventType = binding.autoCompleteTextViewEventType.text.toString().trim()
                    event.eventDescription = binding.editTextEventDescription.text.toString().trim()

                    // Write in the ‘Logcat‘ system and SnackBar
                    showMessage(it)
                }
            }
        }
    }




    private fun showMessage(view: View) {
        Log.d(TAG, event.toString())
        Snackbar.make(view, event.toString(), Snackbar.LENGTH_SHORT).show()
    }


}