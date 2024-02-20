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

        // Adding the Event Listener to Create a new Event
        addEventListener()
    }


    /**
     * This function creates the Event Listener to "Add an Event",
     * it takes the inputs
     */
    private fun addEventListener() {

        with(binding) {
            addEventButton.setOnClickListener {
                if (editTextEventLocation.text.toString().isNotEmpty() && editTextEventName.text.toString()
                        .isNotEmpty()
                ) {

                    // Getting all variables inputs
                    event.eventName = editTextEventName.text.toString().trim()
                    event.eventLocation = editTextEventLocation.text.toString().trim()
                    event.eventDate = editTextEventDate.text.toString().trim()
                    event.eventType = autoCompleteTextViewEventType.text.toString().trim()
                    event.eventDescription = editTextEventDescription.text.toString().trim()

                    // Write in the ‘Logcat‘ system
                    showMessage(it)
                }
            }
        }
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Restore the state of the dropdown or any other relevant information
        binding.autoCompleteTextViewEventType.setText(savedInstanceState.getString("eventType", ""))
    }


    private fun showMessage(view: View) {
        Log.d(TAG, event.toString())
        Snackbar.make(view, event.toString(), Snackbar.LENGTH_SHORT).show()
    }

}