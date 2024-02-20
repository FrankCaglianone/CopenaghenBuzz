package dk.itu.moapd.copenhagenbuzz.fcag

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import dk.itu.moapd.copenhagenbuzz.fcag.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding


    // GUI Variables
    private lateinit var loginButton: Button
    private lateinit var userButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Variables
        loginButton = binding.loginButton
        userButton = binding.userButton


        with(binding) {
            loginButton.setOnClickListener {
                val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
                    putExtra("isLoggedIn", true)
                }
                startActivity(intent)
                finish() // Finish the LoginActivity so the user can't go back to it
            }
        }
    }



//    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
//        menu.findItem(R.id.login).isVisible = \
//        !intent.getBooleanExtra("isLoggedIn", false)
//        menu.findItem(R.id.logout).isVisible = \
//        intent.getBooleanExtra("isLoggedIn", false)
//        return true
//    }

// TO SUBSTITUTE LATER WHEN MAKING ACTUAL LOGIN VALIDATION
//    private fun navigateToMainActivity(isLoggedIn: Boolean) {
//        val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
//            putExtra("isLoggedIn", isLoggedIn)
//        }
//        startActivity(intent)
//        finish() // Finish the LoginActivity so the user can't go back to it
//    }




} // Ending LoginActivity