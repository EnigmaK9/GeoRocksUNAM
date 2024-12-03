package com.enigma.georocks.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.enigma.georocks.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.enigma.georocks.ui.MainActivity

class LoginActivity : AppCompatActivity() {

    // ViewBinding and FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Set up click listeners
        setupListeners()
    }

    /**
     * Sets up click listeners for buttons and clickable elements
     */
    private fun setupListeners() {
        // Login button
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                login(email, password)
            } else {
                showToast("Please enter email and password")
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
     * Logs in the user using Firebase Authentication
     * @param email User's email address
     * @param password User's password
     */
    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login successful
                    navigateToMainActivity()
                } else {
                    // Login failed
                    handleLoginError(task.exception)
                }
            }
    }

    /**
     * Handles login errors
     * @param exception FirebaseAuth exception
     */
    private fun handleLoginError(exception: Exception?) {
        when (exception) {
            is FirebaseAuthInvalidUserException -> showToast("User not registered")
            is FirebaseAuthInvalidCredentialsException -> showToast("Invalid credentials")
            is FirebaseAuthUserCollisionException -> showToast("This user is already registered")
            else -> {
                Log.e("LoginActivity", "Authentication error", exception)
                showToast("Unknown error: ${exception?.message}")
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
