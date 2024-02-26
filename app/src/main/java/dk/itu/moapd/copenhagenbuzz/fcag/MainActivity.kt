package dk.itu.moapd.copenhagenbuzz.fcag

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.copenhagenbuzz.fcag.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    // Binding variable
    private lateinit var binding: ActivityMainBinding


    // A set of private constants used in this class.
    companion object {
        private val TAG = MainActivity::class.qualifiedName
    }


    // An instance of the ‘Event' class.
    private val event : Event = Event("", "", "", "", "")



    /**
     * Initializes the activity. This includes:
     * - Setting up the system window to fit system windows
     * - Inflating the layout using View Binding
     * - Setting up an event listener for creating new events.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     * shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState.
     * Otherwise, it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        // Sets whether the decor view should fit root-level content views for `WindowInsetsCompat`.
        WindowCompat.setDecorFitsSystemWindows(window, false)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Adding the Event Listener to Create a new Event
        createEventListener()
    }






    /**
     * Prepares the options menu for the activity. This function dynamically adjusts the visibility
     * of menu items based on the user's login status.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed; if you return false it will not be shown.
     */
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        Log.d(TAG, "WTF")

        menu.findItem(R.id.login).isVisible =
            !intent.getBooleanExtra("isLoggedIn", false)
        menu.findItem(R.id.logout).isVisible =
            intent.getBooleanExtra("isLoggedIn", false)
        return true
    }






    /**
     * Creates an event listener that responds to clicks on the "Add Event" button by collecting
     * input from the user and creating a new event instance. It validates the user input before
     * proceeding to create an event and displays a message upon successful creation calling
     * showMessage().
     */
    private fun createEventListener() {

        with(binding) {
            contentMain.addEventButton.setOnClickListener {
                if (contentMain.editTextEventLocation.text.toString().isNotEmpty() && contentMain.editTextEventName.text.toString()
                        .isNotEmpty()
                ) {

                    // Getting all variables inputs
                    event.eventName = contentMain.editTextEventName.text.toString().trim()
                    event.eventLocation = contentMain.editTextEventLocation.text.toString().trim()
                    event.eventDate = contentMain.editTextEventDate.text.toString().trim()
                    event.eventType = contentMain.autoCompleteTextViewEventType.text.toString().trim()
                    event.eventDescription = contentMain.editTextEventDescription.text.toString().trim()

                    // Write in the ‘Logcat‘ system and SnackBar
                    showMessage(it)
                }
            }
        }
    }





    /**
     * This function logs the event details and shows a Snackbar with the event information.
     *
     * @param view The view to show.
     */
    private fun showMessage(view: View) {
        Log.d(TAG, event.toString())
        Snackbar.make(view, event.toString(), Snackbar.LENGTH_SHORT).show()
    }







    /**
     * Restores the activity's state during the creation process. This function is called after onStart(),
     * when the activity is being re-initialized from a previously saved state.
     *
     * @param savedInstanceState The Bundle containing the data most recently supplied in onSaveInstanceState.
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Restore the state of the dropdown or any other relevant information
        binding.contentMain.autoCompleteTextViewEventType.setText(savedInstanceState.getString("eventType", ""))
    }




}