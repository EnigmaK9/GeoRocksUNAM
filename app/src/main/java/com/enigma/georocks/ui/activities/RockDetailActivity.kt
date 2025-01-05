package com.enigma.georocks.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
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

        // Setup FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Setup Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbarRockDetail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.rock_details)

        // Get the repository
        repository = (application as GeoRocksApp).repository

        // Retrieve rockId from intent
        val rockId = intent.getStringExtra("ROCK_ID")
        if (rockId == null) {
            Toast.makeText(this, R.string.no_rock_id_provided, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Load rock details
        loadRockDetails(rockId)

        // Initialize the map
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
            R.id.action_route -> {
                openRouteInGoogleMaps()
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
        Toast.makeText(this, R.string.logged_out_successfully, Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java))
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
                        Toast.makeText(
                            this@RockDetailActivity,
                            R.string.rock_details_missing,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@RockDetailActivity,
                        R.string.error_loading_details,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<RockDetailDto>, t: Throwable) {
                Toast.makeText(
                    this@RockDetailActivity,
                    R.string.failed_to_load_details,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun updateUIWithDetails(rockDetail: RockDetailDto) {
        // Title & description
        binding.tvRockTitle.text = rockDetail.title ?: getString(R.string.unknown_title)
        binding.tvRockDescription.text = rockDetail.longDesc ?: getString(R.string.no_description_available)

        // Basic properties
        binding.tvRockType.text = getString(
            R.string.type_label,
            rockDetail.aMemberOf ?: getString(R.string.unknown_type)
        )
        binding.tvRockColor.text = getString(
            R.string.color_label,
            rockDetail.color ?: getString(R.string.unknown_color)
        )
        binding.tvRockHardness.text = getString(
            R.string.hardness_label,
            rockDetail.hardness?.toString() ?: getString(R.string.unknown)
        )
        binding.tvRockFormula.text = getString(
            R.string.formula_label,
            rockDetail.formula ?: getString(R.string.unknown)
        )
        binding.tvRockMagnetic.text = getString(
            R.string.magnetic_label,
            rockDetail.magnetic?.toString() ?: getString(R.string.unknown)
        )
        binding.tvRockHealthRisks.text = getString(
            R.string.health_risks_label,
            rockDetail.healthRisks ?: getString(R.string.none)
        )

        // Localities
        val localitiesList = rockDetail.localities
        if (!localitiesList.isNullOrEmpty()) {
            binding.tvRockLocalities.text = getString(
                R.string.localities_label,
                localitiesList.joinToString()
            )
        } else {
            binding.tvRockLocalities.text = getString(
                R.string.localities_label,
                getString(R.string.unknown)
            )
        }

        // Main image
        rockDetail.image?.let { imageUrl ->
            Glide.with(this).load(imageUrl).into(binding.ivRockImage)
        }

        // Additional images (local var to avoid unsafe calls)
        val imagesList = rockDetail.images
        if (!imagesList.isNullOrEmpty()) {
            val additionalImages = imagesList.joinToString("\n")
            binding.tvRockAdditionalImages.text = getString(
                R.string.additional_images_label,
                additionalImages
            )
        } else {
            binding.tvRockAdditionalImages.text = getString(R.string.no_additional_images)
        }

        // Video
        rockDetail.video?.let { videoUrl ->
            val videoUri = Uri.parse(videoUrl)
            binding.vvRockVideo.setVideoURI(videoUri)
            val mediaController = MediaController(this)
            mediaController.setAnchorView(binding.vvRockVideo)
            binding.vvRockVideo.setMediaController(mediaController)
            binding.vvRockVideo.start()
        }

        // Coordinates
        rockLatitude = rockDetail.latitude
        rockLongitude = rockDetail.longitude
        googleMap?.let { updateMapMarker(it) }

        // Physical properties
        val phys = rockDetail.physicalProperties
        if (phys != null) {
            val crystalSystem = phys.ppCrystalSystem ?: getString(R.string.unknown)
            val luster = phys.ppLuster ?: getString(R.string.unknown)
            val streak = phys.ppStreak ?: getString(R.string.unknown)
            val tenacity = phys.ppTenacity ?: getString(R.string.unknown)
            val cleavage = phys.ppCleavage ?: getString(R.string.unknown)
            val fracture = phys.ppFracture ?: getString(R.string.unknown)
            val density = phys.ppDensity ?: getString(R.string.unknown)

            binding.tvRockPhysicalProperties.text = """
                Crystal System: $crystalSystem
                Luster: $luster
                Streak: $streak
                Tenacity: $tenacity
                Cleavage: $cleavage
                Fracture: $fracture
                Density: $density
            """.trimIndent()
        } else {
            binding.tvRockPhysicalProperties.text = getString(R.string.no_physical_properties)
        }

        // Chemical properties
        val chem = rockDetail.chemicalProperties
        if (chem != null) {
            val classification = chem.cpChemicalClassification ?: getString(R.string.unknown)
            val formula = chem.cpFormula ?: getString(R.string.unknown)

            // Use local variable for commonImpurities
            val impuritiesList = chem.cpCommonImpurities
            val commonImpurities = if (!impuritiesList.isNullOrEmpty()) {
                impuritiesList.joinToString()
            } else {
                getString(R.string.unknown)
            }

            binding.tvRockChemicalProperties.text = """
                Classification: $classification
                Formula: $formula
                Common Impurities: $commonImpurities
            """.trimIndent()
        } else {
            binding.tvRockChemicalProperties.text = getString(R.string.no_chemical_properties)
        }

        // FAQs (use local var to avoid unsafe calls)
        val faqsList = rockDetail.frequentlyAskedQuestions
        if (!faqsList.isNullOrEmpty()) {
            val faqsBulleted = faqsList.joinToString(
                separator = "\n• ",
                prefix = "• "
            )
            binding.tvRockFaqs.text = faqsBulleted
        } else {
            binding.tvRockFaqs.text = getString(R.string.no_faqs)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        updateMapMarker(map)
    }

    private fun updateMapMarker(map: GoogleMap) {
        if (rockLatitude != null && rockLongitude != null) {
            val location = LatLng(rockLatitude!!, rockLongitude!!)
            map.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(binding.tvRockTitle.text.toString())
            )
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
        }
    }

    private fun openRouteInGoogleMaps() {
        if (rockLatitude != null && rockLongitude != null) {
            val gmmIntentUri = Uri.parse("google.navigation:q=$rockLatitude,$rockLongitude")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                setPackage("com.google.android.apps.maps")
            }
            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            } else {
                Toast.makeText(this, R.string.google_maps_not_installed, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, R.string.coordinates_missing, Toast.LENGTH_SHORT).show()
        }
    }
}
