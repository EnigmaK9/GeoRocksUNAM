// File path: app/src/main/java/com/enigma/georocks/data/db/FavoriteRockDao.kt

package com.enigma.georocks.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete

@Dao
interface FavoriteRockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(rock: FavoriteRockEntity)

    @Delete
    suspend fun deleteFavorite(rock: FavoriteRockEntity)

    @Query("SELECT * FROM favorite_rocks WHERE rockId = :rockId LIMIT 1")
    suspend fun getFavoriteById(rockId: String): FavoriteRockEntity?

    @Query("SELECT * FROM favorite_rocks")
    suspend fun getAllFavorites(): List<FavoriteRockEntity>
}
