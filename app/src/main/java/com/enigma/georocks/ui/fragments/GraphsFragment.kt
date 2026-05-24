package com.enigma.georocks.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.enigma.georocks.application.GeoRocksApp
import com.enigma.georocks.data.RockRepository
import com.enigma.georocks.databinding.FragmentGraphsBinding
import kotlinx.coroutines.launch

class GraphsFragment : Fragment() {

    private var _binding: FragmentGraphsBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: RockRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGraphsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = (requireActivity().application as GeoRocksApp).repository

        loadStatistics()
    }

    private fun loadStatistics() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val (total, cutPercent, thinPercent) = repository.getSpecimenStats()

                binding.tvTotalSamplesCount.text = total.toString()

                // Update Chart 1 (Cut Section)
                val cutYesParams = binding.viewCutYes.layoutParams as LinearLayout.LayoutParams
                cutYesParams.weight = cutPercent.toFloat()
                binding.viewCutYes.layoutParams = cutYesParams

                val cutNoParams = binding.viewCutNo.layoutParams as LinearLayout.LayoutParams
                cutNoParams.weight = (100 - cutPercent).toFloat()
                binding.viewCutNo.layoutParams = cutNoParams

                binding.tvCutYesText.text = "Con Corte: $cutPercent%"
                binding.tvCutNoText.text = "Sin Corte: ${100 - cutPercent}%"

                // Update Chart 2 (Thin Section)
                val thinYesParams = binding.viewThinYes.layoutParams as LinearLayout.LayoutParams
                thinYesParams.weight = thinPercent.toFloat()
                binding.viewThinYes.layoutParams = thinYesParams

                val thinNoParams = binding.viewThinNo.layoutParams as LinearLayout.LayoutParams
                thinNoParams.weight = (100 - thinPercent).toFloat()
                binding.viewThinNo.layoutParams = thinNoParams

                binding.tvThinYesText.text = "Con Lámina: $thinPercent%"
                binding.tvThinNoText.text = "Sin Lámina: ${100 - thinPercent}%"

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al cargar estadísticas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
