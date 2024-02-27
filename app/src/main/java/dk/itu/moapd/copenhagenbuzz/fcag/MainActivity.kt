package dk.itu.moapd.copenhagenbuzz.fcag

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.copenhagenbuzz.fcag.databinding.ActivityMainBinding




/**
 * Main Activity class for the Copenhagen Buzz application.
 *
 * This activity is responsible for initializing the application's UI and handling user interactions
 * to create new events. It sets up the content view using view binding and configures the system
 * window to fit the system windows for immersive content display.
 *
 * @property binding An instance of [ActivityMainBinding] for accessing the views in the activity's layout.
 * @property event An instance of [Event] class used to store the details of a new event.
 */
class MainActivity : AppCompatActivity() {

    // Binding variable
    private lateinit var binding: ActivityMainBinding


    // A set of private constants used in this class.
    companion object {
        private val TAG = MainActivity::class.qualifiedName
    }



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

        with(binding) {
            setSupportActionBar(topAppBar)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return true
    }



    /**
     * Prepares the options menu for the activity. This function dynamically adjusts the visibility
     * of menu items based on the user's login status.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed; if you return false it will not be shown.
     */
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.login).isVisible =
            !intent.getBooleanExtra("isLoggedIn", false)
        menu.findItem(R.id.logout).isVisible =
            intent.getBooleanExtra("isLoggedIn", false)
        return true
    }






    /**
     * Restores the activity's state during the creation process. This function is called after onStart(),
     * when the activity is being re-initialized from a previously saved state.
     *
     * @param savedInstanceState The Bundle containing the data most recently supplied in onSaveInstanceState.
     */
//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        // Restore the state of the dropdown or any other relevant information
//        binding.contentMain.autoCompleteTextViewEventType.setText(savedInstanceState.getString("eventType", ""))
//    }




}