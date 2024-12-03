package com.enigma.georocks.ui.fragments

import android.net.Uri // Import for handling URIs
import android.os.Bundle
import android.util.Log
import android.widget.MediaController // Import for media controls
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.enigma.georocks.application.GeoRocksApp
import com.enigma.georocks.data.RockRepository
import com.enigma.georocks.data.remote.model.RockDetailDto
import com.enigma.georocks.databinding.FragmentRockDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RockDetailFragment : Fragment() {

    private var _binding: FragmentRockDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: RockRepository
    private var rockId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            rockId = it.getString(ARG_ROCK_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRockDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = (requireActivity().application as GeoRocksApp).repository

        rockId?.let { id ->
            repository.getRockDetail(id).enqueue(object : Callback<RockDetailDto> {
                override fun onResponse(
                    call: Call<RockDetailDto>,
                    response: Response<RockDetailDto>
                ) {
                    if (response.isSuccessful) {
                        val rockDetail = response.body()
                        if (rockDetail != null) {
                            // Update UI elements with rock details
                            binding.tvTitle.text = rockDetail.title
                            binding.tvLongDesc.text = rockDetail.longDesc
                            // Update other UI elements as needed...

                            // Set up the VideoView
                            if (!rockDetail.video.isNullOrEmpty()) {
                                val videoUri = Uri.parse(rockDetail.video)
                                binding.vvRockVideo.setVideoURI(videoUri)

                                // Add media controls
                                val mediaController = MediaController(requireContext())
                                mediaController.setAnchorView(binding.vvRockVideo)
                                binding.vvRockVideo.setMediaController(mediaController)

                                // Start the video
                                binding.vvRockVideo.start()
                            } else {
                                // Handle the case where there's no video URL
                                Toast.makeText(
                                    requireContext(),
                                    "No video available for this rock",
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.vvRockVideo.visibility = View.GONE
                            }

                            Log.d("RockDetailFragment", "Rock details: $rockDetail")
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Rock details are missing",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Log.e(
                            "RockDetailFragment",
                            "Error fetching rock details: ${response.code()} ${response.message()}"
                        )
                    }
                }

                override fun onFailure(call: Call<RockDetailDto>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "Error retrieving rock details",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("RockDetailFragment", "Error fetching rock details", t)
                }
            })
        }
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
