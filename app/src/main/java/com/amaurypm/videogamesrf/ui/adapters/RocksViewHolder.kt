package com.amaurypm.videogamesrf.ui.adapters

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.amaurypm.videogamesrf.data.remote.model.RockDto
import com.amaurypm.videogamesrf.databinding.RockElementBinding
import com.bumptech.glide.Glide

class RocksViewHolder(
    private val binding: RockElementBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(rock: RockDto) {
        // Log inicial para depurar los valores del objeto RockDto
        Log.d("RocksViewHolder", "Rock Title: ${rock.title}")

        // Asignar los valores iniciales a los TextViews
        binding.tvTitle.text = rock.title ?: "Name Unknown"
        binding.tvType.text = "Loading Type..." // Placeholder inicial
        binding.tvColor.text = "Loading Color..." // Placeholder inicial

        // Cargar la imagen usando Glide con 'thumbnail'
        Glide.with(binding.root.context)
            .load(rock.thumbnail)  // Usa thumbnail en lugar de image
            .into(binding.ivThumbnail)
    }

    // Método para actualizar los detalles adicionales de la roca
    fun updateDetails(type: String?, color: String?) {
        binding.tvType.text = "A member of: ${type ?: "Unknown Type"}"
        binding.tvColor.text = "Color: ${color ?: "Unknown Color"}"

        // Log para confirmar la actualización de detalles
        Log.d("RocksViewHolder", "Updated Details - Type: $type, Color: $color")
    }
}
