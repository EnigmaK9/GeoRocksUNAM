package com.enigma.georocks.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.enigma.georocks.application.GeoRocksApp
import com.enigma.georocks.data.RockRepository
import com.enigma.georocks.databinding.ActivityLoginBinding
import com.enigma.georocks.ui.MainActivity
import com.enigma.georocks.utils.SessionManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    // ViewBinding and Session/Repository
    private lateinit var binding: ActivityLoginBinding
    private lateinit var repository: RockRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Repository
        repository = (application as GeoRocksApp).repository

        // Set up click listeners
        setupListeners()
    }

    /**
     * Sets up click listeners for buttons and clickable elements
     */
    private fun setupListeners() {
        // Login button
        binding.btnLogin.setOnClickListener {
            val username = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                login(username, password)
            } else {
                showToast("Please enter username and password")
            }
        }

        // "Forgot Password?" text
        binding.tvForgotPassword.setOnClickListener {
            navigateToResetPassword()
        }

        // "Create an account" text
        binding.tvRegister.setOnClickListener {
            navigateToRegister()
        }

        // Google Sign-In button (placeholder)
        binding.btnGoogleSignIn.setOnClickListener {
            showToast("Google Sign-In is not implemented yet")
        }
    }

    /**
     * Logs in the user using FastAPI Authentication
     * @param username User's username
     * @param password User's password
     */
    private fun login(username: String, password: String) {
        lifecycleScope.launch {
            try {
                val tokenResponse = repository.login(username, password)
                val sessionManager = SessionManager(this@LoginActivity)
                sessionManager.saveAuthToken(tokenResponse.accessToken)
                sessionManager.saveUsername(username)

                showToast("Login successful")
                navigateToMainActivity()
            } catch (e: Exception) {
                Log.e("LoginActivity", "Authentication error", e)
                showToast("Authentication failed: ${e.localizedMessage ?: "Invalid credentials"}")
            }
        }
    }

    /**
     * Navigates to the main activity after a successful login
     */
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Closes the login activity to prevent going back
    }

    /**
     * Navigates to the reset password activity
     */
    private fun navigateToResetPassword() {
        val intent = Intent(this, ResetPasswordActivity::class.java)
        startActivity(intent)
    }

    /**
     * Navigates to the register activity
     */
    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    /**
     * Displays a Toast message on the screen
     * @param message Message to display
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
