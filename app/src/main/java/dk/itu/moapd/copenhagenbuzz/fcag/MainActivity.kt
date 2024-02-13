package dk.itu.moapd.copenhagenbuzz.fcag

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.core.view.WindowCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.copenhagenbuzz.fcag.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    // GUI Variables
    private lateinit var eventName : EditText
    private lateinit var eventLocation : EditText
    private lateinit var eventDate : EditText
    private lateinit var eventType : AutoCompleteTextView
    private lateinit var eventDescription : EditText
    private lateinit var addEventButton: FloatingActionButton


    // A set of private constants used in this class .
    companion object {
        private val TAG = MainActivity::class.qualifiedName
    }


    // An instance of the ‘Event' class.
    private val event : Event = Event("", "", "", "", "")


    override fun onCreate(savedInstanceState: Bundle?) {
        // Sets whether the decor view should fit root-level content views for `WindowInsetsCompat`.
        WindowCompat.setDecorFitsSystemWindows(window, false)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Variables
        eventName = binding.editTextEventName
        eventLocation = binding.editTextEventLocation
        eventDate = binding.editTextEventDate
        eventType = binding.autoCompleteTextViewEventType
        eventDescription = binding.editTextEventDescription
        addEventButton = binding.addEventButton

        // Creating the dropdown for the Event Types
        createDropdownEventType()

        // Adding the Event Listener to Create a new Event
        addEventListener()
    }


    /**
     *
     * DropDown Item for the Event Type.
     *
     * val eventTypes is the string array of options in res/values/strings.xml "event_types"
     * val arrayAdapter is
     * val autoCompleteTextView is
     *
     */
    private fun createDropdownEventType() {
        val eventTypes = resources.getStringArray(R.array.event_types)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item_layout, eventTypes)
        eventType.setAdapter(arrayAdapter)
    }


    /**
     * This function creates the Event Listener to "Add an Event",
     * it takes the inputs
     */
    private fun addEventListener() {
        addEventButton.setOnClickListener {
            if (eventLocation.text.toString().isNotEmpty() && eventName.text.toString().isNotEmpty()) {

                // Getting all variables inputs
                event.eventName = eventName.text.toString().trim()
                event.eventLocation = eventLocation.text.toString().trim()
                event.eventDate = eventDate.text.toString().trim()
                event.eventType = eventType.text.toString().trim()
                event.eventDescription = eventDescription.text.toString().trim()

                // Write in the ‘Logcat‘ system
                showMessage(it)
            }
        }
    }


    private fun showMessage(view: View) {
        Log.d(TAG, event.toString())
        Snackbar.make(view, event.toString(), Snackbar.LENGTH_SHORT).show()
    }

}