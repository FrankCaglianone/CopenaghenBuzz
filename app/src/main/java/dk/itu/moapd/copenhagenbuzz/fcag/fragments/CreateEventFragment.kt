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
import com.google.android.material.snackbar.Snackbar
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


    // Environment Variables
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


    // An instance of the "Event" and "EventLocation" class.
    private val location: EventLocation = EventLocation(0.0, 0.0, "")
    private val event : Event = Event("", location, "", "", "")



    // Declaring variables used in this class
    private lateinit var crud: CrudOperations
    private lateinit var geocode: Geocoding
    private var imageUri: Uri? = null
    private lateinit var auth: FirebaseAuth





    // Called when the fragment is being created.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Creating an instance of the CrudOperations class.
        crud = CrudOperations()
        // Creating an instance of the Geocoding class.
        geocode = Geocoding()
        // Getting the instance from Firebase Authentication
        auth = FirebaseAuth.getInstance()
    }



    // Called to create the layout of the fragment.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentCreateEventBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Adding the click listener to create a new Event Object
        createEventListener()
        // Adding the click listener to add an image to the Event Object
        addImageEventListener()
    }




    // Called when the view previously created by onCreateView() has been detached from the fragment.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }





    /**
     * This method adds on click listener to the "Add Event" button.
     * It calls validateInputs() to verify that at least the name and the location of the event
     * where inserted avoiding the creation of null events.
     * It than fetches all the information added in a coroutine, needed for the geocoding services
     * to calculate the latitude and longitude of the address of the event.
     * If the user did not add an image for the event it displays a message asking to add an image
     * insuring that the user adds an image for each event (mandatory ex. 09).
     * If the user selected an image, the filename for that image gets created using
     * System.currentTimeMillis().
     * The event is than added to the Firebase Realtime Database and the image is added to the
     * Firebase Storage.
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


                        if (imageUri != null) {
                            val photoFilename = "${System.currentTimeMillis()}.jpg"
                            event.eventPhotoUrl = photoFilename

                            // Add the event to the firebase realtime database
                            crud.addEventToFirebase(event, it)

                            // Upload the image only if the event is created
                            imageUri?.let { uri ->
                                uploadImageToFirebase(uri, photoFilename)
                            }
                        } else {
                            Snackbar.make(it, "Please add an image", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }





    /**
     * Validate the inputs inserted in creating the event, to make sure is Name are location are
     * inserted
     */
    private fun validateInputs(): Boolean {
        // Simple validation for example purposes
        return binding.editTextEventLocation.text.toString().isNotEmpty() && binding.editTextEventName.text.toString().isNotEmpty()
    }






    /**
     * This method adds the click listener to the add Image button to add an image to the Event.
     * It created an AlertDialog asking if the user would like to upload the photo from Google Photo
     * or take a new photo from the camera.
     */
    private fun addImageEventListener() {
       binding.addImageButton.setOnClickListener {
           AlertDialog.Builder(it.context)
               .setTitle("Add Image")
               .setMessage("Would you like to?")
               .setPositiveButton("Upload a photo") { _, _ ->
                   // If the user choose to upload the photo we open Google Photo
                   if (auth.currentUser != null) {
                       selectImage()
                   }
               }
               .setNegativeButton("Take a photo") { _, _ ->
                   // If the user choose to upload the photo we open the device camera
                   dispatchTakePictureIntent()
               }
               .show()
        }
    }








    private fun selectImage() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_IMAGE_SELECT
            )
        } else {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_SELECT)
        }
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






    /**
     * This method is used to upload the image, once selected or taken, to the Firebase Storage.
     * Notes: It gets called in createEventListener().
     */
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