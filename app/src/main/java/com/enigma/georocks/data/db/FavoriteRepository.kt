// File path: app/src/main/java/com/enigma/georocks/data/db/FavoriteRepository.kt

package com.enigma.georocks.data.db

import com.enigma.georocks.data.remote.model.RockDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepository @Inject constructor(private val dao: FavoriteRockDao) {

    // Adds a rock to favorites
    suspend fun addToFavorites(rock: RockDto) = withContext(Dispatchers.IO) {
        val entity = FavoriteRockEntity(
            rockId = rock.id,
            title = rock.title,
            thumbnail = rock.thumbnail
        )
        dao.insertFavorite(entity)
    }

    // Removes a rock from favorites
    suspend fun removeFromFavorites(rock: RockDto) = withContext(Dispatchers.IO) {
        val entity = FavoriteRockEntity(
            rockId = rock.id,
            title = null,        // Title can be set to null or retained based on preference
            thumbnail = null     // Thumbnail can be set to null or retained based on preference
        )
        dao.deleteFavorite(entity)
    }

    // Checks if a rock is favorited
    suspend fun isRockFavorited(rockId: String): Boolean = withContext(Dispatchers.IO) {
        dao.getFavoriteById(rockId) != null
    }

    // Retrieves all favorite rocks
    suspend fun getAllFavorites(): List<FavoriteRockEntity> = withContext(Dispatchers.IO) {
        dao.getAllFavorites()
    }
}
