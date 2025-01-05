package com.enigma.georocks.ui.adapters

import android.content.Intent
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.enigma.georocks.R
import com.enigma.georocks.data.remote.model.RockDto
import com.enigma.georocks.databinding.RockElementBinding
import com.enigma.georocks.ui.activities.RockDetailActivity

class RocksAdapter(
    private var rocks: List<RockDto>,
    private val loadRockDetails: (String, RocksViewHolder) -> Unit // Callback to load details
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
            // Play the click sound
            val mediaPlayer = MediaPlayer.create(holder.itemView.context, R.raw.click_sound)
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener { mp ->
                mp.release()
            }

            // Navigate to RockDetailActivity
            val context = holder.itemView.context
            val intent = Intent(context, RockDetailActivity::class.java).apply {
                putExtra("ROCK_ID", rock.id)
            }
            context.startActivity(intent)
        }
    }

    // Method to update the data in the adapter
    fun updateData(newRocks: List<RockDto>) {
        this.rocks = newRocks
        notifyDataSetChanged()
    }
}
