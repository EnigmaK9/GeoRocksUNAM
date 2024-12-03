package com.enigma.georocks.ui.activities
import com.bumptech.glide.Glide

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.enigma.georocks.R
import com.enigma.georocks.application.GeoRocksApp
import com.enigma.georocks.data.RockRepository
import com.enigma.georocks.data.remote.model.RockDetailDto
import com.enigma.georocks.databinding.ActivityRockDetailBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RockDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityRockDetailBinding
    private lateinit var repository: RockRepository
    private lateinit var auth: FirebaseAuth

    private var googleMap: GoogleMap? = null
    private var rockLatitude: Double? = null
    private var rockLongitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRockDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Set up Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbarRockDetail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Back button
        supportActionBar?.title = getString(R.string.rock_details)

        // Initialize Repository
        repository = (application as GeoRocksApp).repository

        // Get Rock ID from Intent
        val rockId = intent.getStringExtra("ROCK_ID")
        if (rockId != null) {
            loadRockDetails(rockId)
        } else {
            Toast.makeText(this, "No rock ID provided", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Initialize Map Fragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_rock_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        auth.signOut()
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun loadRockDetails(rockId: String) {
        repository.getRockDetail(rockId).enqueue(object : Callback<RockDetailDto> {
            override fun onResponse(call: Call<RockDetailDto>, response: Response<RockDetailDto>) {
                if (response.isSuccessful) {
                    val rockDetail = response.body()
                    if (rockDetail != null) {
                        updateUIWithDetails(rockDetail)
                    } else {
                        Toast.makeText(this@RockDetailActivity, getString(R.string.rock_details_missing), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@RockDetailActivity, getString(R.string.error_loading_details), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RockDetailDto>, t: Throwable) {
                Toast.makeText(this@RockDetailActivity, getString(R.string.failed_to_load_details), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUIWithDetails(rockDetail: RockDetailDto) {
        binding.tvRockTitle.text = rockDetail.title ?: getString(R.string.unknown_title)
        binding.tvRockType.text = getString(R.string.type_label, rockDetail.aMemberOf ?: getString(R.string.unknown_type))
        binding.tvRockColor.text = getString(R.string.color_label, rockDetail.color ?: getString(R.string.unknown_color))
        binding.tvRockHardness.text = getString(R.string.hardness_label, rockDetail.physicalProperties?.hardness?.toString() ?: getString(R.string.unknown))
        binding.tvRockFormula.text = getString(R.string.formula_label, rockDetail.chemicalProperties?.formula ?: getString(R.string.unknown))
        binding.tvRockMagnetic.text = getString(R.string.magnetic_label, rockDetail.physicalProperties?.magnetic?.toString() ?: getString(R.string.unknown))
        binding.tvRockLocalities.text = getString(R.string.localities_label, rockDetail.localities?.joinToString() ?: getString(R.string.unknown))

        // Load Image
        rockDetail.image?.let {
            Glide.with(this).load(it).into(binding.ivRockImage)
        }

        // Setup Video
        rockDetail.video?.let {
            val videoUri = Uri.parse(it)
            binding.vvRockVideo.setVideoURI(videoUri)
            val mediaController = MediaController(this)
            mediaController.setAnchorView(binding.vvRockVideo)
            binding.vvRockVideo.setMediaController(mediaController)
            binding.vvRockVideo.start()
        }

        // Set Map Coordinates
        rockLatitude = rockDetail.latitude
        rockLongitude = rockDetail.longitude
        googleMap?.let { updateMapMarker(it) }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        updateMapMarker(map)
    }

    private fun updateMapMarker(map: GoogleMap) {
        if (rockLatitude != null && rockLongitude != null) {
            val location = LatLng(rockLatitude!!, rockLongitude!!)
            map.addMarker(MarkerOptions().position(location).title(binding.tvRockTitle.text.toString()))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
        }
    }
}
