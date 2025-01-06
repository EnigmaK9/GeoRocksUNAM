// File path: app/src/main/java/com/enigma/georocks/data/db/FavoriteRepository.kt

package com.enigma.georocks.data.db

import com.enigma.georocks.data.remote.model.RockDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Operations related to favorite rocks are managed here
class FavoriteRepository(private val dao: FavoriteRockDao) {

    // A rock is added to favorites
    suspend fun addToFavorites(rock: RockDto) = withContext(Dispatchers.IO) {
        val entity = FavoriteRockEntity(
            rockId = rock.id,
            title = rock.title,
            thumbnail = rock.thumbnail
        )
        dao.insertFavorite(entity)
    }

    // A rock is removed from favorites
    suspend fun removeFromFavorites(rock: RockDto) = withContext(Dispatchers.IO) {
        val entity = FavoriteRockEntity(
            rockId = rock.id,
            title = rock.title,
            thumbnail = rock.thumbnail
        )
        dao.deleteFavorite(entity)
    }

    // It is determined if a rock is favorited
    suspend fun isRockFavorited(rockId: String): Boolean = withContext(Dispatchers.IO) {
        dao.getFavoriteById(rockId) != null
    }

    // All favorite rocks are retrieved
    suspend fun getAllFavorites(): List<FavoriteRockEntity> = withContext(Dispatchers.IO) {
        dao.getAllFavorites()
    }
}
