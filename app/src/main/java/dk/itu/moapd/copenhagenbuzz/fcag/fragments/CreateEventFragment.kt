package dk.itu.moapd.copenhagenbuzz.fcag.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.copenhagenbuzz.fcag.models.Event
import dk.itu.moapd.copenhagenbuzz.fcag.databinding.FragmentCreateEventBinding
import io.github.cdimascio.dotenv.dotenv


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


    // Environment Variables
    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env"
    }
    private val DATABASE_URL = dotenv["DATABASE_URL"]




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
                    addEventToFirebase(event)
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





    // Add the event to the firebase realtime database and display message upon success or failure
    private fun addEventToFirebase(event: Event) {
        val databaseReference = Firebase.database(DATABASE_URL).reference

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        event.userId = userId   // Assign user ID to the event


        val key = userId?.let {
            databaseReference.child("events")
                .child(it)
                .push()
                .key
        } // Generate a unique key for the event


        event.eventId = key // Assign the generated key as the event's ID


        if (key != null) {
            databaseReference.child("events").child(userId).child(key).setValue(event)
                .addOnSuccessListener {
                    Log.d(TAG, "Event added successfully")
                    Snackbar.make(
                        binding.root,
                        "Event added successfully",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to add event", e)
                    Snackbar.make(
                        binding.root,
                        "Failed to add event",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
        }
    }


}