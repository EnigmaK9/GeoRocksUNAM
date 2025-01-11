// File path: /home/enigma/github/kotlin/georocksunam/app/src/main/java/com/enigma/georocks/ui/fragments/FavoriteRocksFragment.kt

package com.enigma.georocks.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.enigma.georocks.databinding.FragmentFavoriteRocksBinding
import com.enigma.georocks.ui.adapters.FavoriteRocksAdapter
import com.enigma.georocks.ui.viewmodels.FavoriteRocksViewModel

class FavoriteRocksFragment : Fragment() {

    private var _binding: FragmentFavoriteRocksBinding? = null
    private val binding get() = _binding!!

    // Initialize the ViewModel
    private val viewModel: FavoriteRocksViewModel by viewModels()

    private lateinit var adapter: FavoriteRocksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavoriteRocksBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Correctly override onViewCreated with the right parameters
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the adapter with a click listener
        adapter = FavoriteRocksAdapter { rock ->
            // Handle click on a favorite rock
            Toast.makeText(requireContext(), "Selected Rock: ${rock.title}", Toast.LENGTH_SHORT).show()
            // Implement navigation to RockDetailActivity or another Fragment as needed
            // Example (assuming you're using Navigation Component):
            /*
            val action = FavoriteRocksFragmentDirections.actionFavoriteRocksFragmentToRockDetailActivity(rock.id)
            findNavController().navigate(action)
            */
        }

        // Set up the RecyclerView
        binding.rvFavoriteRocks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavoriteRocks.adapter = adapter

        // Observe the favorite rocks data
        viewModel.favoriteRocks.observe(viewLifecycleOwner, Observer { rocks ->
            if (rocks.isNullOrEmpty()) {
                binding.tvNoFavorites.visibility = View.VISIBLE
                binding.rvFavoriteRocks.visibility = View.GONE
            } else {
                binding.tvNoFavorites.visibility = View.GONE
                binding.rvFavoriteRocks.visibility = View.VISIBLE
                adapter.submitList(rocks)
            }
        })

        // Observe error messages
        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
