package com.enigma.georocks.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.enigma.georocks.R
import com.enigma.georocks.data.remote.model.LocationResponseDto
import com.enigma.georocks.data.remote.model.SampleResponseDto
import com.enigma.georocks.databinding.ItemLocationCardBinding

class LocationsAdapter(
    private var locations: List<LocationResponseDto>,
    private var samples: List<SampleResponseDto>,
    private val onItemClick: (LocationResponseDto) -> Unit
) : RecyclerView.Adapter<LocationsAdapter.LocationViewHolder>() {

    class LocationViewHolder(val binding: ItemLocationCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val binding = ItemLocationCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return LocationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locations[position]
        holder.binding.tvCountryName.text = location.country
        holder.binding.tvCityName.text = location.name

        // Count rocks collected at this location
        val count = samples.count {
            it.locationName.equals(location.name, ignoreCase = true) &&
                    it.locationCountry.equals(location.country, ignoreCase = true)
        }

        holder.binding.tvLocationSamplesCount.text = if (count == 1) {
            "1 muestra"
        } else {
            "$count muestras"
        }

        // Tap action
        holder.itemView.setOnClickListener {
            onItemClick(location)
        }
    }

    override fun getItemCount(): Int = locations.size

    fun updateData(newLocations: List<LocationResponseDto>, newSamples: List<SampleResponseDto>) {
        locations = newLocations
        samples = newSamples
        notifyDataSetChanged()
    }
}
