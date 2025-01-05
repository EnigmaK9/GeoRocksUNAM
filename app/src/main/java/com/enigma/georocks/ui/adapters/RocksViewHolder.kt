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

    // The context is retrieved from the root view
    private val context = binding.root.context

    fun bind(rock: RockDto) {
        // A log message is recorded to confirm the rock title
        Log.d("RocksViewHolder", "Rock Title: ${rock.title}")

        // The title is assigned, allowing for a fallback if it's empty (though it's non-null)
        binding.tvTitle.text = rock.title.ifBlank {
            context.getString(R.string.unknown_title)
        }

        // A placeholder string resource is used for the type
        binding.tvType.text = context.getString(R.string.loading_type)

        // A placeholder string resource is used for the color
        binding.tvColor.text = context.getString(R.string.loading_color)

        // Glide is used to load the thumbnail into the ImageView
        Glide.with(context)
            .load(rock.thumbnail)
            .into(binding.ivThumbnail)
    }

    fun updateDetails(type: String?, color: String?) {
        // A string resource with a placeholder is used for the "member of" text
        val safeType = type ?: context.getString(R.string.unknown_type)
        binding.tvType.text = context.getString(R.string.member_of_format, safeType)

        // A string resource with a placeholder is used for the color
        val safeColor = color ?: context.getString(R.string.unknown_color)
        binding.tvColor.text = context.getString(R.string.color_format, safeColor)

        // A log message is recorded to confirm that details were updated
        Log.d("RocksViewHolder", "Updated Details - Type: $type, Color: $color")
    }
}
