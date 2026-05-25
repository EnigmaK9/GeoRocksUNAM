package com.enigma.georocks.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.enigma.georocks.R
import com.enigma.georocks.application.GeoRocksApp
import com.enigma.georocks.data.RockRepository
import com.enigma.georocks.data.remote.model.LocationResponseDto
import com.enigma.georocks.data.remote.model.SampleResponseDto
import com.enigma.georocks.databinding.ActivityLocationsBinding
import com.enigma.georocks.ui.adapters.LocationsAdapter
import kotlinx.coroutines.launch

class LocationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocationsBinding
    private lateinit var repository: RockRepository
    private lateinit var adapter: LocationsAdapter

    private var locationsList: List<LocationResponseDto> = emptyList()
    private var samplesList: List<SampleResponseDto> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = (application as GeoRocksApp).repository

        // Toolbar setup
        setSupportActionBar(binding.toolbarLocations)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Explorador de Yacimientos"

        // Recycler setup
        binding.rvLocations.layoutManager = LinearLayoutManager(this)
        adapter = LocationsAdapter(emptyList(), emptyList()) { location ->
            showRocksAtLocation(location)
        }
        binding.rvLocations.adapter = adapter

        loadLocationsAndSamples()
    }

    private fun loadLocationsAndSamples() {
        binding.pbLocationsLoading.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                // Fetch both locations and samples
                locationsList = repository.getLocations()
                
                // Fetch samples directly from the repository API layer
                val rocks = (application as GeoRocksApp).repository
                // We fetch all samples using a local list call
                val rocksList = repository.getRocksApiary() // Wait, let's get the original list from API
                
                // Let's get the original raw samples to match location details perfectly
                samplesList = (application as GeoRocksApp).repository.getRocksStatisticsSamples()
                
                adapter.updateData(locationsList, samplesList)
            } catch (e: Exception) {
                Toast.makeText(this@LocationsActivity, "Error al conectar con el backend", Toast.LENGTH_SHORT).show()
            } finally {
                binding.pbLocationsLoading.visibility = View.GONE
            }
        }
    }

    private fun showRocksAtLocation(location: LocationResponseDto) {
        val matchingSamples = samplesList.filter {
            it.locationName.equals(location.name, ignoreCase = true) &&
                    it.locationCountry.equals(location.country, ignoreCase = true)
        }

        if (matchingSamples.isEmpty()) {
            AlertDialog.Builder(this)
                .setTitle(location.name)
                .setMessage("No hay muestras catalogadas en este yacimiento actualmente.")
                .setPositiveButton("Aceptar", null)
                .show()
            return
        }

        val rockNames = matchingSamples.map { it.rockName }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Rocas en ${location.name}, ${location.country}")
            .setItems(rockNames) { _, which ->
                val selectedSample = matchingSamples[which]
                val intent = Intent(this, RockDetailActivity::class.java).apply {
                    putExtra("ROCK_ID", selectedSample.uid)
                }
                startActivity(intent)
            }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
