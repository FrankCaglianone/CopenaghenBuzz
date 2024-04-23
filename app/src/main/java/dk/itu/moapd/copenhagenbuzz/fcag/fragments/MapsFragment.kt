package dk.itu.moapd.copenhagenbuzz.fcag.fragments

import android.content.pm.PackageManager
import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import dk.itu.moapd.copenhagenbuzz.fcag.databinding.FragmentMapsBinding



import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.copenhagenbuzz.fcag.R
import dk.itu.moapd.copenhagenbuzz.fcag.data.Event
import io.github.cdimascio.dotenv.dotenv

class MapsFragment : Fragment() {


    private var _binding: FragmentMapsBinding? = null

    private val binding
        get() = requireNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }


    // Enviroment Variables
    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env"
    }
    private val DATABASE_URL = dotenv["DATABASE_URL"]


    companion object {
        private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = FragmentMapsBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager
            .findFragmentById(binding.map.id) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }





    private val callback = OnMapReadyCallback { googleMap ->

        // Add a marker in IT University of Copenhagen and move the camera.
        val itu = LatLng(55.6596, 12.5910)
        googleMap.addMarker(MarkerOptions().position(itu).title("IT University of Copenhagen"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(itu))

        // Move the Google Maps UI buttons under the OS top bar.
        googleMap.setPadding(0, 100, 0, 0)

        // Enable the location layer. Request the permission if it is not granted.
        if (checkPermission()) {
            googleMap.isMyLocationEnabled = true
        } else {
            requestUserPermissions()
        }

        addMarkers(googleMap)

        context?.let { openGoogleMaps(googleMap, it) }
    }



    private fun checkPermission() =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED



    private fun requestUserPermissions() {
        if (!checkPermission())
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
    }







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