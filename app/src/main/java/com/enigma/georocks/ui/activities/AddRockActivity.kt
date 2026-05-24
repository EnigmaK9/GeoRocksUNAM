package com.enigma.georocks.ui.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.enigma.georocks.R
import com.enigma.georocks.application.GeoRocksApp
import com.enigma.georocks.data.RockRepository
import com.enigma.georocks.data.remote.model.SampleCreateRequestDto
import com.enigma.georocks.databinding.ActivityAddRockBinding
import kotlinx.coroutines.launch

class AddRockActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddRockBinding
    private lateinit var repository: RockRepository
    private var isEditMode = false
    private var editRockId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve RockRepository
        repository = (application as GeoRocksApp).repository

        // Configure Toolbar
        setSupportActionBar(binding.toolbarAddRock)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Determine Mode: Add or Edit
        val rockId = intent.getStringExtra("ROCK_ID")
        if (!rockId.isNullOrBlank()) {
            isEditMode = true
            editRockId = rockId
            supportActionBar?.title = "Editar Muestra"
            loadRockDetailsForEdit(rockId)
        } else {
            isEditMode = false
            supportActionBar?.title = "Añadir Nueva Muestra"
        }

        // Save Button Click Listener
        binding.btnSaveRock.setOnClickListener {
            saveRock()
        }
    }

    private fun loadRockDetailsForEdit(rockId: String) {
        lifecycleScope.launch {
            try {
                val detail = repository.getRockDetail(rockId)
                binding.etRockName.setText(detail.title)
                binding.etDescription.setText(detail.longDesc)

                // Extract locality and country from detail.color (formatted as "Localidad: Locality, Country")
                val colorText = detail.color ?: ""
                val localityAndCountry = colorText.removePrefix("Localidad: ")
                val parts = localityAndCountry.split(", ")
                if (parts.isNotEmpty()) {
                    binding.etLocationName.setText(parts[0])
                }
                if (parts.size > 1) {
                    binding.etLocationCountry.setText(parts[1])
                }

                // Extract picture filename
                val picUrl = detail.image ?: ""
                val filename = if (picUrl.contains("/")) picUrl.substringAfterLast("/") else picUrl
                binding.etPicture.setText(if (filename.isNotBlank()) filename else "Sin muestra")

                // Set switches
                binding.switchCut.isChecked = detail.aMemberOf?.contains("Corte: Sí") == true
                binding.switchThinSection.isChecked = detail.aMemberOf?.contains("Lámina: Sí") == true

            } catch (e: Exception) {
                Toast.makeText(this@AddRockActivity, "Error al cargar los detalles de la roca", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveRock() {
        val name = binding.etRockName.text?.toString()?.trim() ?: ""
        val locality = binding.etLocationName.text?.toString()?.trim() ?: ""
        val country = binding.etLocationCountry.text?.toString()?.trim() ?: ""
        val description = binding.etDescription.text?.toString()?.trim() ?: ""
        var picture = binding.etPicture.text?.toString()?.trim() ?: ""
        val isCut = binding.switchCut.isChecked
        val isThin = binding.switchThinSection.isChecked

        // Form Validation
        if (name.isBlank()) {
            binding.etRockName.error = "El nombre de la roca es requerido"
            binding.etRockName.requestFocus()
            return
        }
        if (locality.isBlank()) {
            binding.etLocationName.error = "La localidad es requerida"
            binding.etLocationName.requestFocus()
            return
        }
        if (country.isBlank()) {
            binding.etLocationCountry.error = "El país es requerido"
            binding.etLocationCountry.requestFocus()
            return
        }
        if (picture.isBlank()) {
            picture = "Sin muestra"
        }

        val request = SampleCreateRequestDto(
            rockName = name,
            description = description,
            locationName = locality,
            locationCountry = country,
            cut = isCut,
            thinSection = isThin,
            picture = picture
        )

        lifecycleScope.launch {
            try {
                if (isEditMode) {
                    val id = editRockId ?: return@launch
                    repository.updateSample(id, request)
                    showSuccessDialog("Muestra editada exitosamente")
                } else {
                    repository.createSample(request)
                    showSuccessDialog("Muestra añadida exitosamente")
                }
            } catch (e: Exception) {
                Toast.makeText(this@AddRockActivity, "Error al guardar la muestra: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showSuccessDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Éxito")
            .setMessage(message)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
                setResult(RESULT_OK)
                finish()
            }
            .setCancelable(false)
            .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
