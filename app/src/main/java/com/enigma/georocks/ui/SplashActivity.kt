package com.enigma.georocks.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.enigma.georocks.databinding.ActivitySplashBinding
import com.enigma.georocks.ui.activities.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.root.postDelayed({
            val currentUser = auth.currentUser
            if (currentUser != null) {
                // Usuario autenticado
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // Usuario no autenticado
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 2000)
    }
}
