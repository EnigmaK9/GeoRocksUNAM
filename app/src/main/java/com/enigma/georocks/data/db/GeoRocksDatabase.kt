// File path: app/src/main/java/com/enigma/georocks/data/db/GeoRocksDatabase.kt

package com.enigma.georocks.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavoriteRockEntity::class], version = 1, exportSchema = false)
// This abstract class is declared to create the Room database instance
abstract class GeoRocksDatabase : RoomDatabase() {
    // Abstract function to access the FavoriteRockDao
    abstract fun favoriteRockDao(): FavoriteRockDao
}
