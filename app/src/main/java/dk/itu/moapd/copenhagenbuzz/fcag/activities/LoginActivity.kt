package dk.itu.moapd.copenhagenbuzz.fcag.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.WindowCompat
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.snackbar.Snackbar
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


    // This object launches a new activity and receives back some result data.
    private val signInLauncher =
        registerForActivityResult(FirebaseAuthUIActivityResultContract())
        { result -> onSignInResult(result) }



    // This method uses FirebaseUI to create a login activity with the sign-in/sign-up options chose.
    private fun createSignInIntent() {
        // Choose authentication providers.
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
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




    /**
     * When the LoginActivity finishes it returns a result
     * If the user sign-in the application correctly, we redirect the user to the main activity of
     * this application and show a successful Snack bar.
     * Otherwise we show a failure Snack bar.
     */
    private fun onSignInResult(
        result: FirebaseAuthUIAuthenticationResult
    ) {
        when (result.resultCode) {
            RESULT_OK -> {
                // Successfully signed in, start the MainActivity
                showSnackBar("User logged in the app.")
                startMainActivity()
            }
            else -> {
                // Sign in failed.
                showSnackBar("Authentication failed.")
            }
        }
    }



    // Navigate and start Main Activity
    private fun startMainActivity() {
        Intent(this, MainActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }





    // Snack bar helper function
    private fun showSnackBar(message: String) {
        Snackbar.make(
            window.decorView.rootView, message, Snackbar.LENGTH_SHORT
        ).show()
    }





    // Called when the activity is starting.
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        // Create the Sign In intent
        createSignInIntent()
    }


} // Ending LoginActivity