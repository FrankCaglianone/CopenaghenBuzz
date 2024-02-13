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
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}