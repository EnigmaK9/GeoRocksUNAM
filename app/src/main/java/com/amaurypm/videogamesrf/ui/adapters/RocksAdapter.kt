package com.amaurypm.videogamesrf.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amaurypm.videogamesrf.data.remote.model.RockDto
import com.amaurypm.videogamesrf.databinding.RockElementBinding

class RocksAdapter(
    private val rocks: List<RockDto>,
    private val onRockClicked: (RockDto) -> Unit,
    private val loadRockDetails: (String, RocksViewHolder) -> Unit // callback para cargar detalles
) : RecyclerView.Adapter<RocksViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RocksViewHolder {
        val binding = RockElementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RocksViewHolder(binding)
    }

    override fun getItemCount(): Int = rocks.size

    override fun onBindViewHolder(holder: RocksViewHolder, position: Int) {
        val rock = rocks[position]

        // Bind the initial rock data
        holder.bind(rock)

        // Load additional details using the rock ID
        rock.id?.let { loadRockDetails(it, holder) }

        // Set the click listener for the whole item
        holder.itemView.setOnClickListener {
            onRockClicked(rock)
        }
    }
}
