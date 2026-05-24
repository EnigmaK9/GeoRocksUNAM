package com.enigma.georocks.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.enigma.georocks.R
import com.enigma.georocks.application.GeoRocksApp
import com.enigma.georocks.data.RockRepository
import com.enigma.georocks.data.db.FavoriteRepository
import com.enigma.georocks.data.remote.model.RockDetailDto
import com.enigma.georocks.data.remote.model.RockDto
import com.enigma.georocks.databinding.ActivityRockDetailBinding
import com.enigma.georocks.ui.MainActivity
import kotlinx.coroutines.launch

class RockDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRockDetailBinding
    private lateinit var repository: RockRepository

    private val favoriteRepo: FavoriteRepository by lazy {
        (application as GeoRocksApp).favoriteRepository
    }

    private var currentRockId: String? = null
    private var currentRockTitle: String? = null
    private var currentRockThumbnail: String? = null

    private var isFavorite: Boolean = false
    private var favoriteMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRockDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbarRockDetail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.rock_details)

        repository = (application as GeoRocksApp).repository
        val incomingRockId = intent.getStringExtra("ROCK_ID")

        if (incomingRockId.isNullOrBlank()) {
            Toast.makeText(this, R.string.no_rock_id_provided, Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        currentRockId = incomingRockId

        // Fetch rock details asynchronously
        lifecycleScope.launch {
            try {
                isFavorite = favoriteRepo.isRockFavorited(incomingRockId)
                invalidateOptionsMenu()

                val rockDetail: RockDetailDto = repository.getRockDetail(incomingRockId)
                updateUIWithDetails(rockDetail)
            } catch (e: Exception) {
                Log.e("RockDetailActivity", "Failed to load details", e)
                Toast.makeText(
                    this@RockDetailActivity,
                    R.string.failed_to_load_details,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_rock_detail, menu)
        
        // Hide the directions/route action because there is no coordinates in local backend
        menu?.findItem(R.id.action_route)?.isVisible = false
        
        favoriteMenuItem = menu?.findItem(R.id.action_favorite_rock)
        updateFavoriteIcon(isFavorite)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                performLogout()
                true
            }
            R.id.action_favorite_rock -> {
                toggleFavorite()
                true
            }
            R.id.action_view_favorites -> {
                val intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra("SHOW_FAVORITES", true)
                }
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

    private fun toggleFavorite() {
        val rockId = currentRockId ?: return
        lifecycleScope.launch {
            if (!isFavorite) {
                val rockDto = RockDto(
                    id = rockId,
                    thumbnail = currentRockThumbnail,
                    title = currentRockTitle ?: ""
                )
                favoriteRepo.addToFavorites(rockDto)
                isFavorite = true
                updateFavoriteIcon(isFavorite)
                Toast.makeText(this@RockDetailActivity, "Added to favorites.", Toast.LENGTH_SHORT).show()
            } else {
                val rockDto = RockDto(
                    id = rockId,
                    thumbnail = currentRockThumbnail,
                    title = currentRockTitle ?: ""
                )
                favoriteRepo.removeFromFavorites(rockDto)
                isFavorite = false
                updateFavoriteIcon(isFavorite)
                Toast.makeText(this@RockDetailActivity, "Removed from favorites.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateFavoriteIcon(favorited: Boolean) {
        favoriteMenuItem?.let { item ->
            if (favorited) {
                item.setIcon(R.drawable.ic_favorite_filled)
            } else {
                item.setIcon(R.drawable.ic_favorite_border)
            }
        }
    }

    private fun performLogout() {
        com.enigma.georocks.utils.SessionManager(this).clearSession()
        Toast.makeText(this, R.string.logged_out_successfully, Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun updateUIWithDetails(rockDetail: RockDetailDto) {
        currentRockTitle = rockDetail.title
        currentRockThumbnail = rockDetail.image

        binding.tvRockTitle.text = rockDetail.title ?: getString(R.string.unknown_title)
        binding.tvRockDescription.text = rockDetail.longDesc ?: getString(R.string.no_description_available)
        binding.tvRockType.text = rockDetail.aMemberOf ?: getString(R.string.unknown_type)
        binding.tvRockColor.text = rockDetail.color ?: getString(R.string.unknown_color)

        rockDetail.image?.let {
            Glide.with(this)
                .load(it)
                .into(binding.ivRockImage)
        }
    }
}
