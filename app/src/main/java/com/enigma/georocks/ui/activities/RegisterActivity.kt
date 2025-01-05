package com.enigma.georocks.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.enigma.georocks.R
import com.enigma.georocks.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.android.material.snackbar.Snackbar

class RegisterActivity : AppCompatActivity() {

    // Instance of View Binding
    private lateinit var binding: ActivityRegisterBinding

    // Instance of FirebaseAuth
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using View Binding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configure the Toolbar
        setSupportActionBar(binding.toolbarRegister)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Show back button

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Handle the click on the register button
        binding.btnRegister.setOnClickListener {
            val email = binding.etEmailRegister.text.toString().trim()
            val password = binding.etPasswordRegister.text.toString()

            // Basic validations
            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (isValidEmail(email)) {
                    if (isValidPassword(password)) {
                        registerUser(email, password)
                    } else {
                        Toast.makeText(
                            this,
                            getString(R.string.password_too_short),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.invalid_email),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.complete_all_fields),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Handle the click on the back button in the Toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    // Function to register the user in Firebase
    private fun registerUser(email: String, password: String) {
        // Show ProgressBar and disable the register button
        binding.progressBarRegister.visibility = View.VISIBLE
        binding.btnRegister.isEnabled = false

        // Create user with email and password
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                // Hide ProgressBar and enable the register button
                binding.progressBarRegister.visibility = View.GONE
                binding.btnRegister.isEnabled = true

                if (task.isSuccessful) {
                    // Registration successful
                    Snackbar.make(
                        binding.registerCoordinatorLayout,
                        getString(R.string.registration_success),
                        Snackbar.LENGTH_LONG
                    )
                        .setBackgroundTint(ContextCompat.getColor(this, R.color.success_color))
                        .setTextColor(ContextCompat.getColor(this, R.color.snackbar_text_color))
                        .show()
                    finish()
                } else {
                    // Registration failed with specific error handling
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthUserCollisionException) {
                        // Email already registered
                        Snackbar.make(
                            binding.registerCoordinatorLayout,
                            getString(R.string.email_already_registered),
                            Snackbar.LENGTH_LONG
                        )
                            .setBackgroundTint(ContextCompat.getColor(this, R.color.error_color))
                            .setTextColor(ContextCompat.getColor(this, R.color.snackbar_text_color))
                            .show()
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        // Weak password
                        Snackbar.make(
                            binding.registerCoordinatorLayout,
                            getString(R.string.password_too_weak),
                            Snackbar.LENGTH_LONG
                        )
                            .setBackgroundTint(ContextCompat.getColor(this, R.color.error_color))
                            .setTextColor(ContextCompat.getColor(this, R.color.snackbar_text_color))
                            .show()
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        // Invalid email address
                        Snackbar.make(
                            binding.registerCoordinatorLayout,
                            getString(R.string.invalid_email_address),
                            Snackbar.LENGTH_LONG
                        )
                            .setBackgroundTint(ContextCompat.getColor(this, R.color.error_color))
                            .setTextColor(ContextCompat.getColor(this, R.color.snackbar_text_color))
                            .show()
                    } catch (e: Exception) {
                        // Other errors
                        Snackbar.make(
                            binding.registerCoordinatorLayout,
                            getString(R.string.registration_failure) + ": ${e.message}",
                            Snackbar.LENGTH_LONG
                        )
                            .setBackgroundTint(ContextCompat.getColor(this, R.color.error_color))
                            .setTextColor(ContextCompat.getColor(this, R.color.snackbar_text_color))
                            .show()
                    }
                }

            }
    }

    // Function to validate the email format
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Function to validate the password length
    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
}
