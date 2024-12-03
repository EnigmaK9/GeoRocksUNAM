package com.enigma.georocks.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.enigma.georocks.R
import com.enigma.georocks.data.RockRepository
import com.enigma.georocks.data.remote.RetrofitHelper
import com.enigma.georocks.data.remote.model.RockDetailDto
import com.enigma.georocks.databinding.ActivityMainBinding
import com.enigma.georocks.ui.activities.LoginActivity
import com.enigma.georocks.ui.fragments.RocksListFragment
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var repository: RockRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofit = RetrofitHelper().getRetrofit()
        repository = RockRepository(retrofit)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RocksListFragment())
                .commit()
        }

        checkRockDetails()  // Verificar detalles de las rocas
    }

    private fun checkRockDetails() {
        val rockIds = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")

        for (rockId in rockIds) {
            val call = repository.getRockDetail(rockId)  // Cambiado a getRockDetail para coincidir con el método en RockRepository
            call.enqueue(object : Callback<RockDetailDto> {
                override fun onResponse(call: Call<RockDetailDto>, response: Response<RockDetailDto>) {
                    if (response.isSuccessful) {
                        val rockDetail = response.body()
                        // Acceder correctamente a aMemberOf y color
                        Log.d("API", "Rock ID: $rockId, a_member_of: ${rockDetail?.aMemberOf}, color: ${rockDetail?.color}")
                    } else {
                        Log.d("API", "Error retrieving details for Rock ID: $rockId")
                    }
                }

                override fun onFailure(call: Call<RockDetailDto>, t: Throwable) {
                    Log.e("API", "Error retrieving details for Rock ID: $rockId, message: ${t.message}", t)
                }
            })
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}

