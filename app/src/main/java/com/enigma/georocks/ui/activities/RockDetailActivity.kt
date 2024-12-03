package com.enigma.georocks.ui.activities

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.enigma.georocks.application.GeoRocksApp
import com.enigma.georocks.data.RockRepository
import com.enigma.georocks.data.remote.model.RockDetailDto
import com.enigma.georocks.databinding.ActivityRockDetailBinding
import com.bumptech.glide.Glide
import com.enigma.georocks.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RockDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityRockDetailBinding
    private lateinit var repository: RockRepository

    private lateinit var mapFragment: SupportMapFragment
    private var rockLatitude: Double? = null
    private var rockLongitude: Double? = null
    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRockDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = (application as GeoRocksApp).repository

        val rockId = intent.getStringExtra("ROCK_ID")
        if (rockId != null) {
            loadRockDetails(rockId)
        } else {
            Toast.makeText(this, "No rock ID provided", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Inicializar el fragmento del mapa
        mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun loadRockDetails(rockId: String) {
        repository.getRockDetail(rockId).enqueue(object : Callback<RockDetailDto> {
            override fun onResponse(call: Call<RockDetailDto>, response: Response<RockDetailDto>) {
                if (response.isSuccessful) {
                    val rockDetail = response.body()
                    if (rockDetail != null) {
                        // Actualizar la UI con los detalles de la roca
                        binding.tvRockTitle.text = rockDetail.title ?: getString(R.string.unknown_title)
                        binding.tvRockType.text = getString(R.string.type_label, rockDetail.aMemberOf ?: getString(R.string.unknown_type))
                        binding.tvRockColor.text = getString(R.string.color_label, rockDetail.color ?: getString(R.string.unknown_color))
                        binding.tvRockHardness.text = getString(R.string.hardness_label, rockDetail.physicalProperties?.hardness?.toString() ?: getString(R.string.unknown))
                        binding.tvRockFormula.text = getString(R.string.formula_label, rockDetail.chemicalProperties?.formula ?: getString(R.string.unknown))
                        binding.tvRockMagnetic.text = getString(R.string.magnetic_label, rockDetail.physicalProperties?.magnetic?.toString() ?: getString(R.string.unknown))
                        binding.tvRockLocalities.text = getString(R.string.localities_label, rockDetail.localities?.joinToString() ?: getString(R.string.unknown))

                        Glide.with(this@RockDetailActivity)
                            .load(rockDetail.image)
                            .into(binding.ivRockImage)

                        // Configurar el VideoView
                        val videoUri = Uri.parse(rockDetail.video)
                        binding.vvRockVideo.setVideoURI(videoUri)

                        // Agregar controles de reproducción
                        val mediaController = MediaController(this@RockDetailActivity)
                        mediaController.setAnchorView(binding.vvRockVideo)
                        binding.vvRockVideo.setMediaController(mediaController)

                        // Iniciar el video
                        binding.vvRockVideo.start()

                        // Obtener latitud y longitud
                        rockLatitude = rockDetail.latitude
                        rockLongitude = rockDetail.longitude

                        // Si el mapa ya está listo, actualizar el marcador
                        googleMap?.let {
                            updateMapMarker(it)
                        }
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

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        // Si ya tenemos latitud y longitud, actualizamos el marcador
        if (rockLatitude != null && rockLongitude != null) {
            updateMapMarker(map)
        }
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
}
