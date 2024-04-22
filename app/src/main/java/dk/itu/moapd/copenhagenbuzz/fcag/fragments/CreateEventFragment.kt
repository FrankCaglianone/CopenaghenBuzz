package dk.itu.moapd.copenhagenbuzz.fcag.fragments


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dk.itu.moapd.copenhagenbuzz.fcag.CameraX
import dk.itu.moapd.copenhagenbuzz.fcag.CrudOperations
import dk.itu.moapd.copenhagenbuzz.fcag.Geocoding
import dk.itu.moapd.copenhagenbuzz.fcag.R
import dk.itu.moapd.copenhagenbuzz.fcag.data.Event
import dk.itu.moapd.copenhagenbuzz.fcag.data.EventLocation
import dk.itu.moapd.copenhagenbuzz.fcag.databinding.FragmentCreateEventBinding
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch



class CreateEventFragment : Fragment() {


    // Enviroment Variables
    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env"
    }
    private val STORAGE_URL = dotenv["STORAGE_URL"]


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
    private val location: EventLocation = EventLocation(0.0, 0.0, "")
    private val event : Event = Event("", location, "", "", "")



    // Declaring an instance of the CrudOperations class.
    private lateinit var crud: CrudOperations
    private lateinit var geocode: Geocoding


    // Declaring variables for Firebase Storage
    private var imageUri: Uri? = null
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Creating an instance of the CrudOperations class.
        crud = CrudOperations()
        geocode = Geocoding()
        auth = FirebaseAuth.getInstance()
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
        addImageEventListener()
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

                    MainScope().launch {

                        val locationString = binding.editTextEventLocation.text.toString().trim()
                        // getLocationCoordinates(locationString) uses coroutines, needs to be launched in a MainScope
                        event.eventLocation = geocode.getLocationCoordinates(locationString)

                        // Collect inputs
                        event.eventName = binding.editTextEventName.text.toString().trim()
                        event.eventDate = binding.editTextEventDate.text.toString().trim()
                        event.eventType = binding.autoCompleteTextViewEventType.text.toString().trim()
                        event.eventDescription = binding.editTextEventDescription.text.toString().trim()
                        // TODO Fix this UGLINESS
                        val tmp = "${System.currentTimeMillis()}.jpg"
                        event.eventPhotoUrl = tmp

                        // Add the event to the firebase realtime database
                        crud.addEventToFirebase(event, it)
                        imageUri?.let { uri ->
                            uploadImageToFirebase(uri, tmp)
                        }
                    }
                }
            }
        }
    }




    // validate the inputs provided
    private fun validateInputs(): Boolean {
        // Simple validation for example purposes
        return binding.editTextEventLocation.text.toString().isNotEmpty() && binding.editTextEventName.text.toString().isNotEmpty()
    }



    private fun addImageEventListener() {
       binding.addImageButton.setOnClickListener {
           AlertDialog.Builder(it.context)
               .setTitle("Add Image")
               .setMessage("Would you like to?")
               .setPositiveButton("Upload a photo") { dialog, which ->
                   if (auth.currentUser != null) {
                       selectImage()
                   }
               }
               .setNegativeButton("Take a photo") { dialog, which ->
                  openCameraFragment()
               }
               .show()
        }
    }




    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
//            imageUri?.let { uri ->
//                uploadImageToFirebase(uri)
//            }
        }
    }




    private fun uploadImageToFirebase(fileUri: Uri, photoUrl: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val storageReference = Firebase.storage(STORAGE_URL).reference.child("event")

        userId?.let {
            storageReference.child(it).child(photoUrl).putFile(fileUri)
                .addOnSuccessListener {
                    Log.d(TAG, "SUCCESS")
                }
                .addOnFailureListener {
                    Log.d(TAG, "FAIL")
                }
        }
    }



    // TODO FIX IT
    private fun openCameraFragment() {
        val newFragment = CameraX()  // Assuming CameraFragment is your fragment class
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container_view, newFragment)  // Ensure you have a container to place the fragment
            addToBackStack(null)  // Add the transaction to the back stack if needed
            commit()
        }
    }




}