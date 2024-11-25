package com.amaurypm.videogamesrf.ui.adapters

import android.content.Intent
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amaurypm.videogamesrf.R
import com.amaurypm.videogamesrf.data.remote.model.RockDto
import com.amaurypm.videogamesrf.databinding.RockElementBinding
import com.amaurypm.videogamesrf.ui.activities.RockDetailActivity

class RocksAdapter(
    private val rocks: List<RockDto>,
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
}
