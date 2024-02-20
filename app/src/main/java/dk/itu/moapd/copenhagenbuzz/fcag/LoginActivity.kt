package dk.itu.moapd.copenhagenbuzz.fcag

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Button
import android.widget.EditText
import dk.itu.moapd.copenhagenbuzz.fcag.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    companion object {
        private val TAG = LoginActivity::class.qualifiedName
    }


    override fun onCreate(savedInstanceState: Bundle?) {
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



    private fun navigateToMainActivity(isLoggedIn: Boolean) {
        val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
            putExtra("isLoggedIn", isLoggedIn)
        }
        startActivity(intent)
        finish() // Finish the LoginActivity so the user can't go back to it
    }




} // Ending LoginActivity