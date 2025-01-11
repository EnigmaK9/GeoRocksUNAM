// File path: /home/enigma/github/kotlin/georocksunam/app/src/main/java/com/enigma/georocks/ui/adapters/FavoriteRocksAdapter.kt

package com.enigma.georocks.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.enigma.georocks.data.remote.model.RockDto
import com.enigma.georocks.databinding.ItemFavoriteRockBinding

class FavoriteRocksAdapter(
    private val onItemClick: (RockDto) -> Unit
) : ListAdapter<RockDto, FavoriteRocksAdapter.FavoriteRockViewHolder>(FavoriteRockDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteRockViewHolder {
        val binding = ItemFavoriteRockBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FavoriteRockViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteRockViewHolder, position: Int) {
        val rock = getItem(position)
        holder.bind(rock)
    }

    inner class FavoriteRockViewHolder(
        private val binding: ItemFavoriteRockBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(rock: RockDto) {
            binding.tvRockTitle.text = rock.title
            Glide.with(binding.ivRockThumbnail.context)
                .load(rock.thumbnail)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(binding.ivRockThumbnail)

            binding.root.setOnClickListener {
                onItemClick(rock)
            }
        }
    }
}

class FavoriteRockDiffCallback : DiffUtil.ItemCallback<RockDto>() {
    override fun areItemsTheSame(oldItem: RockDto, newItem: RockDto): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RockDto, newItem: RockDto): Boolean {
        return oldItem == newItem
    }
}
