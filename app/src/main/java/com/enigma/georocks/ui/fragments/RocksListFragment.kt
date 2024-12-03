package com.enigma.georocks.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.enigma.georocks.application.GeoRocksApp
import com.enigma.georocks.data.RockRepository
import com.enigma.georocks.data.remote.model.RockDto
import com.enigma.georocks.data.remote.model.RockDetailDto
import com.enigma.georocks.databinding.FragmentRocksListBinding
import com.enigma.georocks.ui.adapters.RocksAdapter
import com.enigma.georocks.ui.adapters.RocksViewHolder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RocksListFragment : Fragment() {

    private var _binding: FragmentRocksListBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: RockRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentRocksListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = (requireActivity().application as GeoRocksApp).repository

        val call: Call<MutableList<RockDto>> = repository.getRocksApiary()

        call.enqueue(object : Callback<MutableList<RockDto>> {
            override fun onResponse(call: Call<MutableList<RockDto>>, response: Response<MutableList<RockDto>>) {
                binding.pbLoading.visibility = View.GONE

                if (response.isSuccessful) {
                    response.body()?.let { rocks ->
                        Log.d("RocksListFragment", "Lista de rocas recibida con éxito: $rocks")

                        // Configura el RecyclerView con el adaptador y el callback correcto
                        binding.rvRocks.apply {
                            layoutManager = LinearLayoutManager(requireContext())
                            adapter = RocksAdapter(rocks) { rockId, viewHolder ->
                                loadRockDetails(rockId, viewHolder)
                            }
                        }
                    } ?: run {
                        Log.e("RocksListFragment", "Respuesta vacía del servidor")
                    }
                } else {
                    Log.e("RocksListFragment", "Error en la respuesta: ${response.code()} ${response.message()}")
                }
            }

            override fun onFailure(call: Call<MutableList<RockDto>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: No connection available", Toast.LENGTH_SHORT).show()
                binding.pbLoading.visibility = View.GONE
            }
        })
    }

    private fun loadRockDetails(rockId: String, viewHolder: RocksViewHolder) {
        repository.getRockDetail(rockId).enqueue(object : Callback<RockDetailDto> {
            override fun onResponse(call: Call<RockDetailDto>, response: Response<RockDetailDto>) {
                if (response.isSuccessful) {
                    val rockDetail = response.body()
                    Log.d("RocksListFragment", "Detalles de la roca - Type: ${rockDetail?.aMemberOf}, Color: ${rockDetail?.color}")

                    // Actualizar el ViewHolder con los detalles adicionales
                    rockDetail?.let { viewHolder.updateDetails(it.aMemberOf, it.color) }
                } else {
                    Log.e("RocksListFragment", "Error al obtener detalles de la roca: ${response.code()} ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RockDetailDto>, t: Throwable) {
                Log.e("RocksListFragment", "Error retrieving rock details", t)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
