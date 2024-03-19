package dk.itu.moapd.copenhagenbuzz.fcag.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.copenhagenbuzz.fcag.R
import dk.itu.moapd.copenhagenbuzz.fcag.databinding.ActivityMainBinding


/**
 * Main Activity class for the Copenhagen Buzz application.
 *
 * This activity serves as the primary entry point of the application, is responsible for
 * initializing the application's UI and setting up navigation. It uses View Binding for direct
 * interaction with UI elements and configures the system window to fit the system windows for
 * immersive content display.
 *
 * @property binding An instance of [ActivityMainBinding] for accessing the views in the activity's layout.
 */
class MainActivity : AppCompatActivity() {

    // Binding variable
    private lateinit var binding: ActivityMainBinding


    // A set of private constants used in this class.
    companion object {
        private val TAG = MainActivity::class.qualifiedName
    }


    // Firebase variable
    private lateinit var auth: FirebaseAuth



    /**
     * Called when the activity is starting. This method is part of the Activity lifecycle in Android development.
     * It performs a check on the user's authentication status and redirects the user to the LoginActivity
     * if they are not currently logged in. This ensures that only authenticated users can access the activity.
     *
     * Overrides:
     * onStart in class Activity
     */
    override fun onStart() {
        super.onStart()
        // Redirect the user to the LoginActivity if they are not logged in.
        auth.currentUser ?: startLoginActivity()
    }




    /**
     * Initializes the activity. This includes:
     * - Setting up the system window to fit system windows
     * - Inflating the layout using View Binding
     * - Configures the Navigation Controller for fragment navigation.
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

        // Initialize Firebase Auth.
        auth = FirebaseAuth.getInstance()

        // Support Action for the Top App Bar
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Create the NavController for the Fragments
        instantiateNavController()
    }



    // Start the login activity using firebase
    private fun startLoginActivity() {
        Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }.let(::startActivity)
    }




    /**
     * Handles the selection of an item in the options menu. This method is triggered whenever an item
     * in the options menu is selected. Specifically, it handles the action for the log out menu item
     * by signing the user out and redirecting them to the LoginActivity.
     *
     * @param item The menu item that was selected.
     * @return Boolean Returns true if the item selection was handled by this method; returns the result of the
     *         superclass onOptionsItemSelected method otherwise. This ensures that menu selections not
     *         explicitly handled by this method are still processed according to Android's default behavior.
     *
     * Overrides:
     * onOptionsItemSelected in class Activity
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        // Log Out Button
        R.id.action_logout -> {
            auth.signOut()
            startLoginActivity()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }



    /**
     * Initializes the options menu for the activity. This method inflates the menu layout,
     * adding items to the action bar if it is present. It's called only once, the first time
     * the options menu is displayed.
     *
     * @param menu The options menu in which you place your items.
     * @return True for the menu to be displayed; if you return false, it will not be shown.
     */
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
     * Sets up the Navigation Controller for managing fragment navigation within the app.
     * It finds the navigation host fragment in the current view hierarchy and links it with
     * the bottom navigation bar for intuitive navigation.
     */
    private fun instantiateNavController() {
        // Search the view hierarchy and fragment for the `NavController` and return it to you.
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container_view) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
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