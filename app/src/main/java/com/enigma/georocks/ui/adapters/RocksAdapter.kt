// File path: app/src/main/java/com/enigma/georocks/ui/adapters/RocksAdapter.kt

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

        // Initial rock data is bound
        holder.bind(rock)

        // Additional details are loaded using the rock ID
        rock.id?.let { loadRockDetails(it, holder) }

        // Click listener is set for the entire item
        holder.itemView.setOnClickListener {
            // Click sound is played
            val mediaPlayer = MediaPlayer.create(holder.itemView.context, R.raw.click_sound)
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener { mp ->
                mp.release()
            }

            // Navigation to RockDetailActivity is initiated
            val context = holder.itemView.context
            val intent = Intent(context, RockDetailActivity::class.java).apply {
                putExtra("ROCK_ID", rock.id)
            }
            context.startActivity(intent)
        }
    }

    // Data in the adapter is updated
    fun updateData(newRocks: List<RockDto>) {
        this.rocks = newRocks
        notifyDataSetChanged()
    }
}
