package com.amaurypm.videogamesrf.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.amaurypm.videogamesrf.application.GeoRocksApp
import com.amaurypm.videogamesrf.data.RockRepository
import com.amaurypm.videogamesrf.data.remote.model.RockDetailDto
import com.amaurypm.videogamesrf.databinding.FragmentRockDetailBinding
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
            repository.getRockDetail(id).enqueue(object : Callback<RockDetailDto> {  // Cambiado a getRockDetail
                override fun onResponse(call: Call<RockDetailDto>, response: Response<RockDetailDto>) {
                    if (response.isSuccessful) {
                        val rockDetail = response.body()
                        // Aquí puedes actualizar la interfaz de usuario con los detalles de la roca
                        binding.tvTitle.text = rockDetail?.title
                        binding.tvLongDesc.text = rockDetail?.longDesc
                        // y otros elementos de UI con los detalles...
                        Log.d("RockDetailFragment", "Detalles de la roca: $rockDetail")
                    } else {
                        Log.e("RockDetailFragment", "Error al obtener detalles de la roca: ${response.code()} ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<RockDetailDto>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error retrieving rock details", Toast.LENGTH_SHORT).show()
                    Log.e("RockDetailFragment", "Error al obtener detalles de la roca", t)
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
