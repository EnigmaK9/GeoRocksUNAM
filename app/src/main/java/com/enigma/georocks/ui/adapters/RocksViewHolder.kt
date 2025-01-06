// ============================
// RocksViewHolder.kt
// ============================


package com.enigma.georocks.ui.adapters

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.enigma.georocks.R
import com.enigma.georocks.data.remote.model.RockDto
import com.enigma.georocks.databinding.RockElementBinding

class RocksViewHolder(
    private val binding: RockElementBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val context = binding.root.context

    fun bind(rock: RockDto) {
        // Safely handle the nullable 'title' by using '?:' to provide a default empty string,
        // then call 'ifBlank' to show a placeholder if it's empty.
        val safeTitle = (rock.title ?: "").ifBlank {
            context.getString(R.string.unknown_title)
        }
        binding.tvTitle.text = safeTitle

        // "Loading" placeholders for type and color
        binding.tvType.text = context.getString(R.string.loading_type)
        binding.tvColor.text = context.getString(R.string.loading_color)

        // Safely load the thumbnail. Glide can handle a null path, but an empty string is passed here for clarity.
        val safeThumbnail = rock.thumbnail ?: ""
        Glide.with(context)
            .load(safeThumbnail)
            .into(binding.ivThumbnail)

        Log.d("RocksViewHolder", "Rock Title: $safeTitle")
    }

    fun updateDetails(type: String?, color: String?) {
        // Provide fallbacks in case 'type' or 'color' are null
        val safeType = type ?: context.getString(R.string.unknown_type)
        binding.tvType.text = context.getString(R.string.member_of_format, safeType)

        val safeColor = color ?: context.getString(R.string.unknown_color)
        binding.tvColor.text = context.getString(R.string.color_format, safeColor)

        Log.d("RocksViewHolder", "Updated Details - Type: $type, Color: $color")
    }
}
