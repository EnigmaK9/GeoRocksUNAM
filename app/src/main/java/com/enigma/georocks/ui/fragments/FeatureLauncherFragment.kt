package com.enigma.georocks.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.enigma.georocks.R
import com.enigma.georocks.databinding.FragmentFeatureLauncherBinding
import com.enigma.georocks.ui.MainActivity
import com.enigma.georocks.ui.activities.InfoActivity
import com.enigma.georocks.ui.activities.AddRockActivity

class FeatureLauncherFragment : Fragment() {

    private var _binding: FragmentFeatureLauncherBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeatureLauncherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Card 1: Search Launcher -> switch to Rocks tab
        binding.cardSearchLauncher.setOnClickListener {
            (activity as? MainActivity)?.selectTab(R.id.navigation_rocks)
        }

        // Card 2: Maps Launcher -> show Toast
        binding.cardMapsLauncher.setOnClickListener {
            Toast.makeText(requireContext(), "Geolocalización y mapas en desarrollo", Toast.LENGTH_SHORT).show()
        }

        // Card 3: Info Launcher -> launch InfoActivity
        binding.cardInfoLauncher.setOnClickListener {
            startActivity(Intent(requireContext(), InfoActivity::class.java))
        }

        // Button: Add New Rock -> launch AddRockActivity
        binding.btnAddNewRock.setOnClickListener {
            startActivity(Intent(requireContext(), AddRockActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
