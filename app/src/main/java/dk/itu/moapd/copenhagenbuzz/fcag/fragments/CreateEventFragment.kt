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
import dk.itu.moapd.copenhagenbuzz.fcag.dbOperations.CrudOperations
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
     * Configures the onClick listener for the "Add Event" button to create an event.
     * This method first validates the critical inputs (event name and location) to ensure they are
     * not empty.
     * If validated, it fetches additional event details asynchronously using coroutines.
     * This includes geocoding the location string to get latitude and longitude.
     * It checks if an image has been added for the event. If not, it prompts the user to add one (mandatory ex. 09).
     * If an image is selected, it generates a unique filename using System.currentTimeMillis(),
     * creates the event in Firebase Realtime Database,and uploads the image to Firebase Storage.
     *
     * Pre-conditions:
     * - Event name and location must be provided.
     * - User must be authenticated as this function interacts with Firebase.
     *
     * Post-conditions:
     * - Creates an event entry in Firebase Realtime Database.
     * - Uploads an associated image to Firebase Storage, if provided.
     * - Displays an error message if an image is not provided.
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
     * Validates that the basic inputs for creating an event (event name and location) are not empty.
     * This is essential to avoid creating null events.
     *
     * @return Boolean true if both name and location inputs are filled, false otherwise.
     */
    private fun validateInputs(): Boolean {
        return binding.editTextEventLocation.text.toString().isNotEmpty() && binding.editTextEventName.text.toString().isNotEmpty()
    }







    /**
     * Sets up a click listener on the 'addImageButton' for adding an image to an event.
     * When clicked, an AlertDialog is displayed asking the user whether they would like to upload a
     * photo from Google Photo or take a new photo using the camera.
     *
     * - Selects an image from storage if the "Upload a photo" option is chosen.
     * - Captures a photo with the camera if the "Take a photo" option is chosen.
     */
    private fun addImageEventListener() {
       binding.addImageButton.setOnClickListener {
           AlertDialog.Builder(it.context)
               .setTitle("Add Image")
               .setMessage("Would you like to?")
               .setPositiveButton("Upload a photo") { _, _ ->
                   // Open Google Photo for image selection if the user is authenticated
                   if (auth.currentUser != null) {
                       selectImage()
                   }
               }
               .setNegativeButton("Take a photo") { _, _ ->
                   // Open the device camera to take a new photo
                   dispatchTakePictureIntent()
               }
               .show()
        }
    }







    /**
     * Initiates an intent to select an image from the external storage (Google photos).
     * Checks if the permission to read external storage is granted.
     * If permission is not granted, it requests the necessary permission.
     * Otherwise, it opens the image picker where a user can select an image.
     * Uses `REQUEST_IMAGE_SELECT` as the request code to handle the result in `onActivityResult`.
     */
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








    /**
     * Initiates an intent to capture an image using the device's camera.
     * Checks if the camera permission is granted.
     * If permission is not granted, it requests the necessary permission.
     * Otherwise, it opens the camera to take a photo.
     * Uses `REQUEST_IMAGE_CAPTURE` as the request code to handle the result in `onActivityResult`.
     */
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








    /**
     * Handles the result from either selecting an image from storage or capturing an image with the camera.
     * Updates the `imageUri` with the URI of the selected or captured image.
     * This method is triggered by the `onActivityResult` callback after image selection or capture.
     *
     * @param requestCode The integer request code originally supplied to `startActivityForResult()`,
     * allowing you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
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
     * Uploads the image of the event to Firebase Storage.
     * The image is stored within a child directory named after the user's unique ID, and under a
     * specific file name.
     * On successful upload, a success log is recorded; on failure, a failure log is recorded.
     *
     * @param fileUri The URI of the file to upload. This should be a content URI pointing to the image.
     * @param photoUrl The destination path within the storage where the image will be stored.
     *
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







    /**
     * Converts a Bitmap image into a content URI by inserting the image into the system Media Store.
     * The image is first compressed to JPEG format at the highest quality.
     *
     * @param inContext The context from which the content resolver is accessed.
     * @param inImage The Bitmap image to be converted into a URI.
     * @return Uri representing the location of the image in the Media Store.
     */
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






    /**
     * Handles the result of the permission request for using the camera.
     * If the camera permission is granted, it initiates the intent to take a picture.
     * This method is triggered by the system when the user responds to a permission request.
     *
     * @param requestCode The integer request code associated with the permission request.
     * @param permissions The array of permissions that were requested.
     * @param grantResults The array containing the results of the permission requests.
     * This array contains PackageManager.PERMISSION_GRANTED or PackageManager.PERMISSION_DENIED.
     * It matches the order of the 'permissions' array.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            }
        }
    }



}