package com.amaurypm.videogamesrf.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amaurypm.videogamesrf.application.GeoRocksApp
import com.amaurypm.videogamesrf.data.RockRepository
import com.amaurypm.videogamesrf.data.remote.model.RockDetailDto
import com.amaurypm.videogamesrf.databinding.ActivityRockDetailBinding
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RockDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRockDetailBinding
    private lateinit var repository: RockRepository

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
    }

    private fun loadRockDetails(rockId: String) {
        repository.getRockDetail(rockId).enqueue(object : Callback<RockDetailDto> {
            override fun onResponse(call: Call<RockDetailDto>, response: Response<RockDetailDto>) {
                if (response.isSuccessful) {
                    val rockDetail = response.body()
                    if (rockDetail != null) {
                        binding.tvRockTitle.text = rockDetail.title ?: "Unknown Title"
                        binding.tvRockType.text = "Type: ${rockDetail.aMemberOf ?: "Unknown Type"}"
                        binding.tvRockColor.text = "Color: ${rockDetail.color ?: "Unknown Color"}"
                        binding.tvRockHardness.text = "Hardness: ${rockDetail.physicalProperties?.hardness ?: "Unknown"}"
                        binding.tvRockFormula.text = "Formula: ${rockDetail.chemicalProperties?.formula ?: "Unknown"}"
                        binding.tvRockMagnetic.text = "Magnetic: ${rockDetail.physicalProperties?.crystalSystem ?: "Unknown"}"
                        binding.tvRockLocalities.text = "Localities: ${rockDetail.localities?.joinToString() ?: "Unknown"}"

                        Glide.with(this@RockDetailActivity)
                            .load(rockDetail.image)
                            .into(binding.ivRockImage)
                    } else {
                        Toast.makeText(this@RockDetailActivity, "Rock details are missing", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@RockDetailActivity, "Error loading rock details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RockDetailDto>, t: Throwable) {
                Toast.makeText(this@RockDetailActivity, "Failed to load rock details", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
