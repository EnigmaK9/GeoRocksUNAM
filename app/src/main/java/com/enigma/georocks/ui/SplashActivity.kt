// File path: app/src/main/java/com/enigma/georocks/ui/SplashActivity.kt

package com.enigma.georocks.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.enigma.georocks.databinding.ActivitySplashBinding
import com.enigma.georocks.ui.activities.LoginActivity
import com.enigma.georocks.utils.SessionManager

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sessionManager = SessionManager(this)

        binding.root.postDelayed({
            if (sessionManager.isLoggedIn()) {
                // Authenticated user
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // Unauthenticated user
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 2000)
    }
}
