// File path: /home/enigma/github/kotlin/georocksunam/app/src/main/java/com/enigma/georocks/ui/viewmodels/FavoriteRocksViewModel.kt

package com.enigma.georocks.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enigma.georocks.data.db.FavoriteRepository
import com.enigma.georocks.data.remote.model.RockDto
import com.enigma.georocks.data.db.FavoriteRockEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteRocksViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {


    private val _favoriteRocks = MutableLiveData<List<RockDto>>()
    val favoriteRocks: LiveData<List<RockDto>> get() = _favoriteRocks

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    init {
        fetchFavoriteRocks()
    }

    private fun fetchFavoriteRocks() {
        viewModelScope.launch {
            try {
                val favoriteEntities: List<FavoriteRockEntity> = favoriteRepository.getAllFavorites()

                // Map FavoriteRockEntity to RockDto
                val rocksDto: List<RockDto> = favoriteEntities.map { entity ->
                    RockDto(
                        id = entity.rockId,          // Mapping rockId to id
                        thumbnail = entity.thumbnail,
                        title = entity.title
                    )
                }

                _favoriteRocks.postValue(rocksDto)
            } catch (e: Exception) {
                _errorMessage.postValue("Error al cargar favoritos: ${e.message}")
            }
        }
    }

    // Optional: Add methods to add/remove favorites and update LiveData accordingly
}
