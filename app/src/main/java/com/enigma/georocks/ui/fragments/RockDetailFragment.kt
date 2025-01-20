package com.enigma.georocks.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.enigma.georocks.R
import com.enigma.georocks.application.GeoRocksApp
import com.enigma.georocks.data.RockRepository
import com.enigma.georocks.data.db.FavoriteRepository
import com.enigma.georocks.data.remote.model.RockDetailDto
import com.enigma.georocks.data.remote.model.RockDto
import com.enigma.georocks.databinding.FragmentRockDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RockDetailFragment : Fragment() {

    private var _binding: FragmentRockDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: RockRepository
    private lateinit var favoriteRepo: FavoriteRepository

    private var rockId: String? = null
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            rockId = it.getString(ARG_ROCK_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRockDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = (requireActivity().application as GeoRocksApp).repository
        favoriteRepo = (requireActivity().application as GeoRocksApp).favoriteRepository

        rockId?.let { id ->
            lifecycleScope.launch {
                try {
                    val rockDetail: RockDetailDto = repository.getRockDetail(id)
                    withContext(Dispatchers.Main) {
                        binding.tvTitle.text = rockDetail.title
                        binding.tvLongDesc.text = rockDetail.longDesc
                        binding.tvType.text = "Type: ${rockDetail.aMemberOf}"
                        binding.tvColor.text = "Color: ${rockDetail.color}"

                        if (!rockDetail.video.isNullOrEmpty()) {
                            val videoUri = Uri.parse(rockDetail.video)
                            binding.vvRockVideo.setVideoURI(videoUri)
                            val mediaController = MediaController(requireContext())
                            mediaController.setAnchorView(binding.vvRockVideo)
                            binding.vvRockVideo.setMediaController(mediaController)
                            binding.vvRockVideo.start()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "No video available for this rock",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.vvRockVideo.visibility = View.GONE
                        }

                        isFavorite = favoriteRepo.isRockFavorited(id)
                        Log.i("RockDetailFragment", "Initial isFavorite: $isFavorite")
                        updateHeartIcon(isFavorite)
                        Log.i("RockDetailFragment", "Rock details: $rockDetail")
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "Error retrieving rock details",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Log.e("RockDetailFragment", "Error fetching rock details", e)
                }
            }
        }

        binding.ivFavorite.setOnClickListener {
            rockId?.let { id ->
                lifecycleScope.launch {
                    try {
                        if (!isFavorite) {
                            val rockDetail = repository.getRockDetail(id)
                            val rockDto = RockDto(
                                id = id,
                                title = rockDetail.title ?: "Unknown",
                                thumbnail = rockDetail.image ?: ""
                            )
                            repository.addToFavorites(rockDto)
                            isFavorite = true
                            Log.i("RockDetailFragment", "isFavorite set to true")
                            updateHeartIcon(isFavorite)
                            Toast.makeText(
                                requireContext(),
                                "Added to favorites",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.i("RockDetailFragment", "Rock ID $id added to favorites.")
                        } else {
                            val rockDto = RockDto(
                                id = id,
                                title = null,
                                thumbnail = null
                            )
                            repository.removeFromFavorites(rockDto)
                            isFavorite = false
                            Log.i("RockDetailFragment", "isFavorite set to false")
                            updateHeartIcon(isFavorite)
                            Toast.makeText(
                                requireContext(),
                                "Removed from favorites",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.i("RockDetailFragment", "Rock ID $id removed from favorites.")
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Operation failed", Toast.LENGTH_SHORT)
                            .show()
                        Log.e("RockDetailFragment", "Favorite operation failed", e)
                    }
                }
            }
        }
    }

    private fun updateHeartIcon(favorite: Boolean) {
        Log.i("RockDetailFragment", "updateHeartIcon called with favorite: $favorite")
        if (favorite) {
            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_filled)
            binding.ivFavorite.setImageDrawable(drawable)
            Log.i("RockDetailFragment", "Set favorite icon to filled.")
        } else {
            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_border)
            binding.ivFavorite.setImageDrawable(drawable)
            Log.i("RockDetailFragment", "Set favorite icon to border.")
        }
        binding.ivFavorite.setColorFilter(null) // Elimina cualquier filtro de color
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_ROCK_ID = "rock_id"

        @JvmStatic
        fun newInstance(rockId: String) = RockDetailFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ROCK_ID, rockId)
            }
        }
    }
}
