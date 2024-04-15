package dk.itu.moapd.copenhagenbuzz.fcag.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dk.itu.moapd.copenhagenbuzz.fcag.CrudOperations
import dk.itu.moapd.copenhagenbuzz.fcag.data.Event
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



    // Declaring an instance of the CrudOperations class.
    private lateinit var crud: CrudOperations




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Creating an instance of the CrudOperations class.
        crud = CrudOperations()
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
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
                if (validateInputs()) {

                    // Collect inputs
                    event.eventName = binding.editTextEventName.text.toString().trim()
                    event.eventLocation = binding.editTextEventLocation.text.toString().trim()
                    event.eventDate = binding.editTextEventDate.text.toString().trim()
                    event.eventType = binding.autoCompleteTextViewEventType.text.toString().trim()
                    event.eventDescription = binding.editTextEventDescription.text.toString().trim()


                    // Add the event to the firebase realtime database
                    crud.addEventToFirebase(event, it)
                }
            }
        }
    }




    // validate the inputs provided
    private fun validateInputs(): Boolean {
        // Simple validation for example purposes
        return binding.editTextEventLocation.text.toString().isNotEmpty() &&
                binding.editTextEventName.text.toString().isNotEmpty()
    }

}