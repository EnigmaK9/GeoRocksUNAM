// File path: app/src/main/java/com/enigma/georocks/ui/activities/FavoriteRocksActivity.kt

package com.enigma.georocks.ui.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.enigma.georocks.R
import com.enigma.georocks.databinding.ActivityFavoriteRocksBinding
import com.enigma.georocks.ui.fragments.FavoriteRocksFragment

class FavoriteRocksActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteRocksBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteRocksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configure the Toolbar
        setSupportActionBar(binding.toolbarFavoriteRocks)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.favorite_rocks)

        // Add the FavoriteRocksFragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.favorite_fragment_container, FavoriteRocksFragment())
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle the back button in the Toolbar
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
