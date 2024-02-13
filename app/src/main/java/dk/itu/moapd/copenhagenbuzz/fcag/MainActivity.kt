package dk.itu.moapd.copenhagenbuzz.fcag

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.core.view.WindowCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
//    private val event : Event = Event("", "", "", "", "")



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

        createDropdownEventType()
        Log.d(TAG, "Ciao!")
        okok()
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
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.auto_complete_text_view_event_type)
        autoCompleteTextView.setAdapter(arrayAdapter)
    }



    private fun okok() {
//        addEventButton.setOnClickListener {
//            if (eventLocation.text.toString().isNotEmpty() && eventName.text.toString().isNotEmpty()) {
//                event.setEventName(
//                    eventName.text.toString().trim()
//                )

                // Write in the ‘Logcat‘ system
//                showMessage()
//            }
//        }
    }




//    private fun showMessage() {
//        Log.d(TAG, event.toString())
//    }












}