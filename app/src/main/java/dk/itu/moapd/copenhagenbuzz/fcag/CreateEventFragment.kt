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


    // Binding
    private var _binding: FragmentCreateEventBinding? = null

    // Binding
    private val binding
        get() = requireNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }


    // A set of private constants used in this class.
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

        // Adding the Event Listener to Create a new Event
        createEventListener()

        //  binding.apply { }
    }





    /**
     * Creates an event listener that responds to clicks on the "Add Event" button by collecting
     * input from the user and creating a new event instance. It validates the user input before
     * proceeding to create an event and displays a message upon successful creation calling
     * showMessage().
     */
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





    /**
     * This function logs the event details and shows a Snack bar with the event information.
     *
     * @param view The view to show.
     */
    private fun showMessage(view: View) {
        Log.d(TAG, event.toString())
        Snackbar.make(view, event.toString(), Snackbar.LENGTH_SHORT).show()
    }




    /**
     * Restores the activity's state during the creation process. This function is called after onStart(),
     * when the activity is being re-initialized from a previously saved state.
     *
     * @param savedInstanceState The Bundle containing the data most recently supplied in onSaveInstanceState.
     */
//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        // Restore the state of the dropdown or any other relevant information
//        binding.contentMain.autoCompleteTextViewEventType.setText(savedInstanceState.getString("eventType", ""))
//    }







    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}