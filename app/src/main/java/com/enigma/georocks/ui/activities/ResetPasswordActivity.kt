package com.enigma.georocks.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.enigma.georocks.databinding.ActivityResetPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.snackbar.Snackbar
import android.widget.Toast
import android.view.MenuItem
import androidx.core.app.NavUtils

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar la Toolbar (si está presente en el layout)
        setSupportActionBar(binding.toolbarResetPassword)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Mostrar botón de retroceso

        auth = FirebaseAuth.getInstance()

        binding.btnSendResetEmail.setOnClickListener {
            val email = binding.etEmailReset.text.toString().trim()
            if (email.isNotEmpty()) {
                if (isValidEmail(email)) {
                    sendResetEmail(email)
                } else {
                    Toast.makeText(this, "Por favor, ingresa un correo electrónico válido", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, ingresa tu correo electrónico", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Manejar el botón de retroceso en la Toolbar
        return when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun sendResetEmail(email: String) {
        // Mostrar ProgressBar
        binding.progressBarReset.visibility = android.view.View.VISIBLE
        binding.btnSendResetEmail.isEnabled = false

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                // Ocultar ProgressBar
                binding.progressBarReset.visibility = android.view.View.GONE
                binding.btnSendResetEmail.isEnabled = true

                if (task.isSuccessful) {
                    Snackbar.make(binding.resetPasswordCoordinatorLayout, "Correo de restablecimiento enviado", Snackbar.LENGTH_LONG).show()
                    finish()
                } else {
                    val errorMessage = task.exception?.message ?: "Error al enviar correo de restablecimiento"
                    Snackbar.make(binding.resetPasswordCoordinatorLayout, errorMessage, Snackbar.LENGTH_LONG).show()
                }
            }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
