// File path: /home/enigma/github/kotlin/georocksunam/app/src/main/java/com/enigma/georocks/ui/MainActivity.kt

package com.enigma.georocks.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.enigma.georocks.R
import com.enigma.georocks.application.GeoRocksApp
import com.enigma.georocks.data.RockRepository
import com.enigma.georocks.databinding.ActivityMainBinding
import com.enigma.georocks.ui.activities.LoginActivity
import com.enigma.georocks.ui.fragments.FavoriteRocksFragment
import com.enigma.georocks.ui.fragments.RocksListFragment
import com.enigma.georocks.ui.fragments.FeatureLauncherFragment
import com.enigma.georocks.ui.fragments.GraphsFragment
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var repository: RockRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the RockRepository from GeoRocksApp
        repository = (application as GeoRocksApp).repository

        // Setup bottom navigation listener
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.navigation_rocks -> {
                    supportActionBar?.title = "GeoRocks - Rocas"
                    RocksListFragment()
                }
                R.id.navigation_launcher -> {
                    supportActionBar?.title = "GeoRocks - Lanzador"
                    FeatureLauncherFragment()
                }
                R.id.navigation_graphs -> {
                    supportActionBar?.title = "GeoRocks - Estadísticas"
                    GraphsFragment()
                }
                R.id.navigation_favorites -> {
                    supportActionBar?.title = "GeoRocks - Favoritas"
                    FavoriteRocksFragment()
                }
                else -> RocksListFragment()
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            true
        }

        // If no saved state is present, decide which fragment to show based on the intent
        if (savedInstanceState == null) {
            val showFavorites = intent.getBooleanExtra("SHOW_FAVORITES", false)
            if (showFavorites) {
                binding.bottomNavigation.selectedItemId = R.id.navigation_favorites
            } else {
                binding.bottomNavigation.selectedItemId = R.id.navigation_rocks
            }
        }

        // Optionally, a quick log or demonstration can be placed here
        checkRockDetails() // existing method from your code
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /**
     * A switch statement is used to handle menu selections.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                com.enigma.georocks.utils.SessionManager(this).clearSession()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            R.id.action_open_search -> {
                /*openSearchFragment()*/
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * A method is used to navigate to RocksSearchFragment.

    private fun openSearchFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, RocksSearchFragment()) //  <-- The new search fragment
            .addToBackStack(null)
            .commit()
    }
     */
    /**
     * This function checks details of several rocks for demonstration (existing code).
     */
    private fun checkRockDetails() {
        val rockIds = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        lifecycleScope.launch {
            for (rockId in rockIds) {
                try {
                    val rockDetail = repository.getRockDetail(rockId)
                    // Logs info for debugging
                } catch (e: Exception) {
                    // Error handling
                }
            }
        }
    }

    fun selectTab(tabId: Int) {
        binding.bottomNavigation.selectedItemId = tabId
    }
}
