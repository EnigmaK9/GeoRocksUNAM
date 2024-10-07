package com.amaurypm.videogamesrf.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.amaurypm.videogamesrf.ui.MainActivity
import com.amaurypm.videogamesrf.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Lógica para esperar 2 segundos antes de lanzar el MainActivity
        binding.root.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Para que no vuelva al Splash al presionar "atrás"
        }, 2000) // 2000 milisegundos = 2 segundos
    }
}
