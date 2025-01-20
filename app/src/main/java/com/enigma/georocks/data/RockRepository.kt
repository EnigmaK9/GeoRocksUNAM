// File path: app/src/main/java/com/enigma/georocks/data/RockRepository.kt

package com.enigma.georocks.data

import com.enigma.georocks.data.db.FavoriteRockDao
import com.enigma.georocks.data.db.FavoriteRockEntity
import com.enigma.georocks.data.remote.api.RockApiService
import com.enigma.georocks.data.remote.model.RockDetailDto
import com.enigma.georocks.data.remote.model.RockDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RockRepository(
    private val apiService: RockApiService,
    private val favoriteRockDao: FavoriteRockDao
) {

    // Fetch rock details from the API using suspend function
    suspend fun getRockDetail(id: String): RockDetailDto = withContext(Dispatchers.IO) {
        apiService.getRockDetail(id)
    }

    // Fetch list of rocks from the API using suspend function
    suspend fun getRocksApiary(): MutableList<RockDto> = withContext(Dispatchers.IO) {
        apiService.getRocks()
    }

    // Add a rock to favorites using RockDto
    suspend fun addToFavorites(rockDto: RockDto) = withContext(Dispatchers.IO) {
        val entity = FavoriteRockEntity(
            rockId = rockDto.id,
            title = rockDto.title,
            thumbnail = rockDto.thumbnail
        )
        favoriteRockDao.insertFavorite(entity)
    }

    // Remove a rock from favorites using RockDto
    suspend fun removeFromFavorites(rockDto: RockDto) = withContext(Dispatchers.IO) {
        val entity = FavoriteRockEntity(
            rockId = rockDto.id,
            title = null,        // Title can be set to null or retained based on preference
            thumbnail = null     // Thumbnail can be set to null or retained based on preference
        )
        favoriteRockDao.deleteFavorite(entity)
    }

    // Check if a rock is favorited
    suspend fun isRockFavorited(rockId: String): Boolean = withContext(Dispatchers.IO) {
        favoriteRockDao.getFavoriteById(rockId) != null
    }

    // Retrieve all favorite rocks
    suspend fun getAllFavoriteRocks(): List<FavoriteRockEntity> = withContext(Dispatchers.IO) {
        favoriteRockDao.getAllFavorites()
    }
}
