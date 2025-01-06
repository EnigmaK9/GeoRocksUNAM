// ============================
// MainActivity.kt
// ============================

package com.enigma.georocks.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.enigma.georocks.R
import com.enigma.georocks.application.GeoRocksApp
import com.enigma.georocks.data.RockRepository
import com.enigma.georocks.databinding.ActivityMainBinding
import com.enigma.georocks.ui.activities.LoginActivity
import com.enigma.georocks.ui.fragments.RocksListFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var repository: RockRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // The RockRepository is retrieved from GeoRocksApp.
        repository = (application as GeoRocksApp).repository

        if (savedInstanceState == null) {
            // The RocksListFragment is shown when activity is first created.
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RocksListFragment())
                .commit()
        }

        // The rock details are verified for demonstration.
        checkRockDetails()
    }

    /**
     * The details of several rocks are fetched, each one by its ID.
     * The suspend function is invoked from within a coroutine using lifecycleScope.
     */
    private fun checkRockDetails() {
        val rockIds = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")

        lifecycleScope.launch {
            // Each rock's details are fetched in a try/catch block to handle possible errors.
            for (rockId in rockIds) {
                try {
                    val rockDetail = repository.getRockDetail(rockId)
                    Log.d(
                        "API",
                        "Rock ID: $rockId, a_member_of: ${rockDetail.aMemberOf}, color: ${rockDetail.color}"
                    )
                } catch (e: Exception) {
                    Log.e(
                        "API",
                        "Error retrieving details for Rock ID: $rockId, message: ${e.message}",
                        e
                    )
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /**
     * A menu option is provided to log out the user.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                // The user is signed out of FirebaseAuth and taken back to the Login screen.
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
