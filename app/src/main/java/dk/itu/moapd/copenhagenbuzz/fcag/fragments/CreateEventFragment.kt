package dk.itu.moapd.copenhagenbuzz.fcag.fragments


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dk.itu.moapd.copenhagenbuzz.fcag.CrudOperations
import dk.itu.moapd.copenhagenbuzz.fcag.locationServices.Geocoding
import dk.itu.moapd.copenhagenbuzz.fcag.data.Event
import dk.itu.moapd.copenhagenbuzz.fcag.data.EventLocation
import dk.itu.moapd.copenhagenbuzz.fcag.databinding.FragmentCreateEventBinding
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


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
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_IMAGE_SELECT = 2
        private const val REQUEST_CAMERA_PERMISSION = 101
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


                        val photoFilename = "${System.currentTimeMillis()}.jpg"
                        event.eventPhotoUrl = photoFilename

                        // Add the event to the firebase realtime database
                        crud.addEventToFirebase(event, it)

                        // Upload the image only if the event is created
                        imageUri?.let { uri ->
                            uploadImageToFirebase(uri, photoFilename)
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
                   dispatchTakePictureIntent()
               }
               .show()
        }
    }




    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_SELECT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val fileUri = getImageUri(requireContext(), imageBitmap)
            imageUri = fileUri
        } else if (requestCode == REQUEST_IMAGE_SELECT && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
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







    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Image",
            null
        )
        return Uri.parse(path)
    }



    private fun dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_IMAGE_CAPTURE
            )
        } else {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            }
        }
    }



}