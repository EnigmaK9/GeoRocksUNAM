package com.enigma.georocks.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.enigma.georocks.R
import com.enigma.georocks.application.GeoRocksApp
import com.enigma.georocks.data.RockRepository
import com.enigma.georocks.data.remote.model.RockDetailDto
import com.enigma.georocks.data.remote.model.RockDto
import com.enigma.georocks.databinding.FragmentRocksListBinding
import com.enigma.georocks.ui.activities.LoginActivity
import com.enigma.georocks.ui.adapters.RocksAdapter
import com.enigma.georocks.ui.adapters.RocksViewHolder
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RocksListFragment : Fragment() {

    private var _binding: FragmentRocksListBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: RockRepository
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRocksListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = (requireActivity().application as GeoRocksApp).repository
        auth = FirebaseAuth.getInstance()

        setupToolbar()
        loadRocks()
    }

    private fun setupToolbar() {
        binding.toolbarRocksList.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_logout -> {
                    performLogout()
                    true
                }
                else -> false
            }
        }
    }

    private fun performLogout() {
        auth.signOut()
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun loadRocks() {
        binding.pbLoading.visibility = View.VISIBLE
        val call: Call<MutableList<RockDto>> = repository.getRocksApiary()

        call.enqueue(object : Callback<MutableList<RockDto>> {
            override fun onResponse(call: Call<MutableList<RockDto>>, response: Response<MutableList<RockDto>>) {
                binding.pbLoading.visibility = View.GONE

                if (response.isSuccessful) {
                    response.body()?.let { rocks ->
                        binding.rvRocks.apply {
                            layoutManager = LinearLayoutManager(requireContext())
                            adapter = RocksAdapter(rocks) { rockId, viewHolder ->
                                loadRockDetails(rockId, viewHolder)
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Error loading data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MutableList<RockDto>>, t: Throwable) {
                Toast.makeText(requireContext(), "No connection available", Toast.LENGTH_SHORT).show()
                binding.pbLoading.visibility = View.GONE
            }
        })
    }

    private fun loadRockDetails(rockId: String, viewHolder: RocksViewHolder) {
        repository.getRockDetail(rockId).enqueue(object : Callback<RockDetailDto> {
            override fun onResponse(call: Call<RockDetailDto>, response: Response<RockDetailDto>) {
                if (response.isSuccessful) {
                    val rockDetail = response.body()
                    rockDetail?.let { detail ->
                        viewHolder.updateDetails(detail.aMemberOf, detail.color)
                    }
                }
            }

            override fun onFailure(call: Call<RockDetailDto>, t: Throwable) {
                // Handle failure case
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
