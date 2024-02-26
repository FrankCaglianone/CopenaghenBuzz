package dk.itu.moapd.copenhagenbuzz.fcag

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.WindowCompat
import dk.itu.moapd.copenhagenbuzz.fcag.databinding.ActivityLoginBinding



class LoginActivity : AppCompatActivity() {

    // Binding variable
    private lateinit var binding: ActivityLoginBinding


    // A set of private constants used in this class .
    companion object {
        private val TAG = LoginActivity::class.qualifiedName
    }





    /**
     * Initializes the activity. This includes:
     * - Setting up the system window to fit system windows
     * - Inflating the layout using View Binding
     * - Establishing click listeners for both the login and guest buttons to navigate to the
     *   MainActivity with appropriate login status.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     * shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState.
     * Otherwise, it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        with(binding) {
            loginButton.setOnClickListener {
                navigateToMainActivity(true)
                Log.d(TAG, "isLoggedIn = true")
            }
            guestButton.setOnClickListener {
                navigateToMainActivity(false)
                Log.d(TAG, "isLoggedIn = false")
            }
        }
    }





    /**
     * Navigates to the [MainActivity] and passes the login status as an extra in the intent.
     * This method also finishes the current activity to prevent the user from returning to the
     * login screen.
     *
     * @param isLoggedIn A boolean indicating whether the user has logged in (true) or is
     * continuing as a guest (false).
     */
    private fun navigateToMainActivity(isLoggedIn: Boolean) {
        val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
            putExtra("isLoggedIn", isLoggedIn)
        }
        startActivity(intent)
        finish() // Finish the LoginActivity so the user can't go back to it
    }


} // Ending LoginActivity