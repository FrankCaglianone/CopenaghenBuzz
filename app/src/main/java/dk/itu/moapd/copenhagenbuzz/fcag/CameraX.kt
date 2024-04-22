package dk.itu.moapd.copenhagenbuzz.fcag





import android.os.Bundle
import androidx.fragment.app.Fragment
import dk.itu.moapd.copenhagenbuzz.fcag.databinding.CameraxBinding
import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.copenhagenbuzz.fcag.models.DataViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



class CameraX : Fragment()  {

        private var _binding: CameraxBinding? = null


        private val binding
            get() = requireNotNull(_binding) {
                "Cannot access binding because it is null. Is the view visible?"
            }


        /**
         * A view model to manage the data access to the database. Using lazy initialization to create
         * the view model instance when the user access the object for the first time.
         */
        private val viewModel: DataViewModel by activityViewModels()


        /**
         * The camera selector allows to select a camera or return a filtered set of cameras.
         */
        private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA


        /**
         * This instance provides `takePicture()` functions to take a picture to memory or save to a
         * file, and provides image metadata.
         */
        private var imageCapture: ImageCapture? = null


        /**
         * The latest image taken by the video device stream.
         */
        private var imageUri: Uri? = null


        /**
         * A set of static attributes used in this activity class.
         */
        companion object {
            private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
        }


        /**
         * This object launches a new permission dialog and receives back the user's permission.
         */
        private val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->
            cameraPermissionResult(result)
        }


        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View = CameraxBinding.inflate(inflater, container, false).also {
            _binding = it
        }.root



        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            // Request camera permissions.
            if (checkPermission())
                startCamera()
            else
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)

            // The current selected camera.
            viewModel.selector.observe(viewLifecycleOwner) {
                cameraSelector = it ?: CameraSelector.DEFAULT_BACK_CAMERA
            }

            // Define the UI behavior.
            binding.apply {

                // Set up the listener for take photo button.
                buttonImageCapture.setOnClickListener {
                    takePhoto()
                }

                // Set up the listener for the change camera button.
                buttonCameraSwitch.apply {

                    // Disable the button until the camera is set up
                    isEnabled = false

                    setOnClickListener {
                        viewModel.onCameraSelectorChanged(
                            if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA)
                                CameraSelector.DEFAULT_BACK_CAMERA
                            else
                                CameraSelector.DEFAULT_FRONT_CAMERA
                        )

                        // Re-start use cases to update selected camera.
                        startCamera()
                    }
                }

                // Set up the listener for the photo view button.
                buttonImageViewer.setOnClickListener {
                    imageUri?.let { uri ->
                        val bundle = bundleOf("ARG_IMAGE" to uri.toString())

                        // Navigate to the `ImageFragment` passing the `Image` instance as an argument.
                        requireActivity().findNavController(R.id.fragment_container_view)
//                            .navigate(R.id.action_main_to_image, bundle)
                    }
                }
            }
        }




        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }

        /**
         * Handles the result of camera permission request.
         *
         * @param isGranted A boolean indicating whether the camera permission is granted or not. If
         *      true, starts the camera. If false, finishes the activity.
         */
        private fun cameraPermissionResult(isGranted: Boolean) {
            // Use the takeIf function to conditionally execute code based on the permission result
            isGranted.takeIf { it }?.run {
                startCamera()
            } ?: requireActivity().finish()
        }

        /**
         * This method checks if the user allows the application uses the camera to take photos for this
         * application.
         *
         * @return A boolean value with the user permission agreement.
         */
        private fun checkPermission() =
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

        /**
         * Enabled or disabled a button to switch cameras depending on the available cameras.
         */
        private fun updateCameraSwitchButton(provider: ProcessCameraProvider) {
            binding.buttonCameraSwitch.isEnabled = try {
                hasBackCamera(provider) && hasFrontCamera(provider)
            } catch (exception: CameraInfoUnavailableException) {
                false
            }
        }

        /**
         * Returns true if the device has an available back camera. False otherwise.
         */
        private fun hasBackCamera(provider: ProcessCameraProvider) =
            provider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)

        /**
         * Returns true if the device has an available front camera. False otherwise.
         */
        private fun hasFrontCamera(provider: ProcessCameraProvider) =
            provider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)

        /**
         * This method is used to start the video camera device stream.
         */
        private fun startCamera() {

            // Create an instance of the `ProcessCameraProvider` to bind the lifecycle of cameras to the
            // lifecycle owner.
            val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

            // Add a listener to the `cameraProviderFuture`.
            cameraProviderFuture.addListener({

                // Used to bind the lifecycle of cameras to the lifecycle owner.
                val cameraProvider = cameraProviderFuture.get()

                // Video camera streaming preview.
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

                // Set up the image capture by getting a reference to the `ImageCapture`.
                imageCapture = ImageCapture.Builder().build()

                try {
                    // Unbind use cases before rebinding.
                    cameraProvider.unbindAll()

                    // Bind use cases to camera.
                    cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture)

                    // Update the camera switch button.
                    updateCameraSwitchButton(cameraProvider)

                } catch(ex: Exception) {
                    showSnackBar("Use case binding failed: $ex")
                }

            }, ContextCompat.getMainExecutor(requireContext()))
        }

        /**
         * This method is used to save a frame from the video camera device stream as a JPG photo.
         */
        private fun takePhoto() {

            // Get a stable reference of the modifiable image capture use case.
            val imageCapture = imageCapture ?: return

            // Create a timestamped filename to save the photo.
            val timestamp = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
            val filename = "IMG_$timestamp.jpg"

            // Set desired name and type of captured image to be used by the contentResolver when
            // writing captured image to MediaStore.
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            }

            // Create the output file option to store the captured image in MediaStore.
            val outputFileOptions = ImageCapture.OutputFileOptions
                .Builder(
                    requireActivity().contentResolver,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                .build()

            // Set up image capture listener, which is triggered after photo has been taken.
            imageCapture.takePicture(
                outputFileOptions,
                ContextCompat.getMainExecutor(requireContext()),
                object : ImageCapture.OnImageSavedCallback {

                    /**
                     * Called when an image has been successfully saved.
                     */
                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        imageUri = output.savedUri
                        showSnackBar("Photo capture succeeded: $filename.jpg")
                    }

                    /**
                     * Called when an error occurs while attempting to save an image.
                     *
                     * @param exception An `ImageCaptureException` that contains the type of error, the
                     *      error message and the throwable that caused it.
                     */
                    override fun onError(exception: ImageCaptureException) {
                        showSnackBar("Photo capture failed: ${exception.message}")
                    }
                }
            )
        }


        private fun showSnackBar(message: String) {
            Snackbar.make(
                binding.root, message, Snackbar.LENGTH_SHORT
            ).show()
        }




}