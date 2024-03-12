package dk.itu.moapd.copenhagenbuzz.fcag.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.WindowCompat
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.copenhagenbuzz.fcag.R
import dk.itu.moapd.copenhagenbuzz.fcag.databinding.ActivityLoginBinding



/**
 * LoginActivity is responsible for handling user login actions within the Copenhagen Buzz application.
 *
 * This activity presents the user with options to log in or continue as a guest. Depending on the user's choice,
 * it navigates to the MainActivity with the login state passed as an Intent extra. The activity uses view binding
 * to interact with the layout components efficiently.
 *
 * @property binding An instance of [ActivityLoginBinding] for accessing the views in the activity's layout.
 */
class LoginActivity : AppCompatActivity() {

    // Binding variable
    private lateinit var binding: ActivityLoginBinding


    // A set of private constants used in this class .
    companion object {
        private val TAG = LoginActivity::class.qualifiedName
    }




    private val signInLauncher =
        registerForActivityResult(FirebaseAuthUIActivityResultContract())
        { result -> onSignInResult(result) }



    private fun createSignInIntent() {
        // Choose authentication providers.
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
//            AuthUI.IdpConfig.PhoneBuilder().build(),
//            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        // Create and launch sign-in intent.
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(false)
            .setLogo(R.drawable.ic_launcher_foreground)
            .setTheme(R.style.Theme_CopenhagenBuzz)
            .apply {
                setTosAndPrivacyPolicyUrls(
                    "https://firebase.google.com/terms/",
                    "https://firebase.google.com/policies/…"
                )
            }
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(
        result: FirebaseAuthUIAuthenticationResult
    ) {
        when (result.resultCode) {
            RESULT_OK -> {
                // Successfully signed in.
//                showSnackBar("User logged in the app.")
                startMainActivity()
            }
            else -> {
                // Sign in failed.
//                showSnackBar("Authentication failed.")
            }
        }
    }
    private fun startMainActivity() {
        Intent(this, MainActivity::class.java).apply {
            startActivity(this)
            finish()
        }
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
        createSignInIntent()
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