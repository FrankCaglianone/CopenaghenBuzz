package dk.itu.moapd.copenhagenbuzz.fcag.fragments

import android.content.pm.PackageManager
import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dk.itu.moapd.copenhagenbuzz.fcag.databinding.FragmentMapsBinding



import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.copenhagenbuzz.fcag.locationServices.LocationService
import dk.itu.moapd.copenhagenbuzz.fcag.R
import dk.itu.moapd.copenhagenbuzz.fcag.data.Event
import io.github.cdimascio.dotenv.dotenv

class MapsFragment : Fragment() {

    // Binding
    private var _binding: FragmentMapsBinding? = null
    private val binding
        get() = requireNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }


    // Environment Variables
    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env"
    }
    private val DATABASE_URL = dotenv["DATABASE_URL"]


    // A set of private constants used in this class.
    companion object {
        private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
        private const val TAG = "MapsFragment"
    }


    // Initializing variables used for user location fetching
    private lateinit var receiver: BroadcastReceiver
    private var latitude: Double? = null
    private var longitude: Double? = null




    // Called when the fragment is being created.
    /**
     * As soon as the fragment is created it checks if location permissions are enabled.
     * If not it asks the user to enable them, this is done because upon fragment creation we start
     * fetching the user location to than set up the CameraUpdate to focus on the user's current
     * location calling the method startLocalizationService().
     * Once the current location of the user is fetched we call initializeMap() to initialize
     * Google map's Map.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if location permissions are enabled otherwise ask for them
        if (!checkPermission()) {
            requestUserPermissions()
        }


        // Initialize and register the receiver
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                latitude = intent.getDoubleExtra("latitude", 0.0)
                longitude = intent.getDoubleExtra("longitude", 0.0)
                Log.d(TAG, latitude.toString())
                Log.d(TAG, longitude.toString())

                // Once the location is received, initialize the map. Note: done for Camera Update
                initializeMap()
            }
        }
        val filter = IntentFilter(LocationService.LOCATION_UPDATE_ACTION)
        requireActivity().registerReceiver(receiver, filter)


        // Start fetching the user location when fragment is created
        startLocalizationService()
    }




    // Called to create the layout of the fragment.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = FragmentMapsBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root





    // Called after the view created by onCreateView() has been created and ensures that the view hierarchy is fully initialized.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }




    // Called when the view previously created by onCreateView() has been detached from the fragment.
    override fun onDestroyView() {
        super.onDestroyView()

        // Stop fetching the user location when fragment is destroyed
        stopLocalizationService()

        // Unregister the receiver to prevent memory leaks
        requireActivity().unregisterReceiver(receiver)

        _binding = null
    }





    /**
     * This function initializes Google's Map using the callback.
     * The function gets called only after the Broadcast Receiver received the user location
     * to set up the CameraUpdate to focus on the current user's location.
     */
    private fun initializeMap() {
        // Check if latitude and longitude are available
        if (latitude != null && longitude != null) {
                val mapFragment = childFragmentManager.findFragmentById(binding.map.id) as SupportMapFragment?
                mapFragment?.getMapAsync(callback)
        } else {
            Snackbar.make(requireView(), "fetching user location", Snackbar.LENGTH_LONG).show()
        }
    }







    /**
     * This is the callback passed to getMapAsync in initializeMap().
     * This callback:
     *      - sets up the CameraUpdate to focus on the user's current position
     *      - sets the padding of the map
     *      - double checks if the location permissions are enabled
     *      - add the markers for all the events in the database
     *      - creates the dialogs with the information of each event and the possibility to get
     *          directions to the location of such event in google maps
     */
    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->

        if(latitude != null && longitude != null) {
            val userPosition = LatLng(latitude!!, longitude!!)
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(userPosition))
        }

        // Move the Google Maps UI buttons under the OS top bar.
        googleMap.setPadding(0, 100, 0, 0)

        // Enable the location layer. Request the permission if it is not granted.
        if (checkPermission()) {
            googleMap.isMyLocationEnabled = true
        } else {
            requestUserPermissions()
        }

        // Add the markers for all the events in the database
        addMarkers(googleMap)

        // Set up the dialog to show event info and open location directions in google maps
        context?.let { openGoogleMaps(googleMap, it) }
    }


    /**
     * This function simply checks for location permissions and returns a boolean
     *      - true: if both of them are granted
     *      - false: if one of them or both are not grated
     */
    private fun checkPermission() =
        ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED



    /**
     * This function request the user to enable location permissions.
     * It gets called in case location permissions were not previously enabled by the user
     */
    private fun requestUserPermissions() {
        if (!checkPermission())
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
    }





    /**
     * This function is called on fragment creation to start fetching the user location to than
     * set up the CameraUpdate to focus on the user's current position.
     * The process continues in background until the fragment gets destroyed.
     */
    private fun startLocalizationService() {
        Intent(requireContext(), LocationService::class.java).apply {
            action = LocationService.ACTION_START
            requireActivity().startService(this)
        }
    }





    /**
     * This function is called on fragment destruction to stop fetching the current user position.
     * This is done purposely to minimize battery usage during the app utilization if the user
     * is not looking at the map
     */
    private fun stopLocalizationService() {
        Intent(requireContext(), LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
            requireActivity().startService(this)
        }
    }






    /**
     * This function fetches all the events created from the Firebase realtime database and sets up
     * a marker for it on the map.
     * It also saves all the useful information of that event to than display them in a dialog when
     * the marker is clicked; such as:
     *      - name
     *      - location (Address)
     *      - date
     *      - type
     *      - description
     *
     */
    private fun addMarkers(map: GoogleMap) {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            val userEventsRef = Firebase.database(DATABASE_URL).reference.child("copenhagen_buzz")
                .child("events")
                .child(user.uid)


            userEventsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        snapshot.children.forEach { child ->
                            val event = child.getValue(Event::class.java)
                            event?.let { event ->
                                event.eventLocation?.latitude?.let { latitude ->
                                    event.eventLocation?.longitude?.let { longitude ->
                                        val location = LatLng(latitude, longitude)
                                        val markerOptions = MarkerOptions()
                                            .position(location)
                                            .title(event.eventName)
                                            .snippet("Location:${event.eventLocation!!.address} \n Date:${event.eventDate} \n Type: ${event.eventType} \n Description: ${event.eventDescription}")

                                        map.addMarker(markerOptions)
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error fetching data", error.toException())
                }
            })

        }
    }







    /**
     * This function sets the click listeners for the markers in the map.
     * It creates an AlertDialog displaying the Event name and all useful info about that event.
     * It also asks if we would like to get the directions to that event in google maps and if
     * clicking yes, it automatically opens goggle maps fetching the user location and calculating
     * the shortest path to get to that event.
     */
    private fun openGoogleMaps(map: GoogleMap, context: Context) {
        map.setOnMarkerClickListener { marker ->

            val inflater = LayoutInflater.from(context)
            val dialogView = inflater.inflate(R.layout.dialog_marker_map, null)

            dialogView.findViewById<TextView>(R.id.marker_details).text = marker.snippet
            dialogView.findViewById<TextView>(R.id.marker_open_maps).text = "Do you want to get directions in Google Maps?"


            AlertDialog.Builder(context)
                .setTitle(marker.title)
                .setView(dialogView) // Set the inflated layout as view

                .setPositiveButton("Yes") { dialog, which ->
                    // User clicked yes, open Google Maps with directions
                    val gmmIntentUri = Uri.parse("google.navigation:q=${marker.position.latitude},${marker.position.longitude}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                        startActivity(mapIntent)
                    }
                }
                .setNegativeButton("No", null) // User clicked no, just dismiss the dialog
                .show()
            true // Return true to indicate we've handled this event
        }
    }

}