package dk.itu.moapd.copenhagenbuzz.fcag.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dk.itu.moapd.copenhagenbuzz.fcag.CrudOperations
import dk.itu.moapd.copenhagenbuzz.fcag.Geocoding
import dk.itu.moapd.copenhagenbuzz.fcag.data.Event
import dk.itu.moapd.copenhagenbuzz.fcag.data.EventLocation
import dk.itu.moapd.copenhagenbuzz.fcag.databinding.FragmentCreateEventBinding
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


class CreateEventFragment : Fragment() {


    // Binding
    private var _binding: FragmentCreateEventBinding? = null

    // Binding
    private val binding
        get() = requireNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }


//    private val dotenv = dotenv {
//        directory = "/assets"
//        filename = "env"
//    }
//    private val apiKey = dotenv["GEOCODING_API_KEY"]


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




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Creating an instance of the CrudOperations class.
        crud = CrudOperations()
        geocode = Geocoding()
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

                    MainScope().launch {

                        val locationString = binding.editTextEventLocation.text.toString().trim()
                        // getLocationCoordinates(locationString) uses coroutines, needs to be launched in a MainScope
                        event.eventLocation = geocode.getLocationCoordinates(locationString)

                        // Collect inputs
                        event.eventName = binding.editTextEventName.text.toString().trim()
                        event.eventDate = binding.editTextEventDate.text.toString().trim()
                        event.eventType = binding.autoCompleteTextViewEventType.text.toString().trim()
                        event.eventDescription = binding.editTextEventDescription.text.toString().trim()

                        // Add the event to the firebase realtime database
                        crud.addEventToFirebase(event, it)
                    }
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




//    private suspend fun getLocationCoordinates(location: String): EventLocation {
//        val baseUrl = "https://geocode.maps.co/search"
//        val encodedLocation = URLEncoder.encode(location, "UTF-8")
//        val urlString = "$baseUrl?q=$encodedLocation&api_key=$apiKey"
//
//        return withContext(Dispatchers.IO) {
//            try {
//                val url = URL(urlString)
//                val connection = url.openConnection() as HttpURLConnection
//                connection.requestMethod = "GET"
//                connection.setRequestProperty("Content-type", "application/json")
//                connection.setRequestProperty("Accept", "application/json")
//                connection.connectTimeout = 10000
//                connection.readTimeout = 10000
//
//                val reader = InputStreamReader(connection.inputStream)
//                val response = StringBuilder()
//                val bufferedReader = BufferedReader(reader)
//
//                bufferedReader.useLines { lines ->
//                    lines.forEach { line ->
//                        response.append(line.trim())
//                    }
//                }
//
//                val jsonArray = JSONArray(response.toString())
//
//                if (jsonArray.length() > 0) {
//                    val firstLocation = jsonArray.getJSONObject(0)
//                    val latitude = firstLocation.optString("lat")
//                    val longitude = firstLocation.optString("lon")
//                    Log.d(TAG, "IT WORKS lat:$latitude long:$longitude")
//                    EventLocation(latitude.toDouble(), longitude.toDouble(), location)
//                } else {
//                    EventLocation(null, null, "No location found")
//                }
//            } catch (e: Exception) {
//                EventLocation(null, null, "Error: ${e.message}")
//            }
//        }
//    }






}