// ===============================================================
// File path: app/src/main/java/com/enigma/georocks/data/db/FavoriteDatabase.kt
// The Room database class is declared here. It includes the FavoriteRockDao.
// ===============================================================

package com.enigma.georocks.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// The version number is incremented each time the schema is changed
@Database(entities = [FavoriteRockEntity::class], version = 1, exportSchema = false)
// This abstract class is declared for Room to generate the database
abstract class FavoriteDatabase : RoomDatabase() {

    // A reference to the DAO is provided
    abstract fun favoriteRockDao(): FavoriteRockDao

    companion object {
        @Volatile
        private var INSTANCE: FavoriteDatabase? = null

        // A singleton pattern is used to retrieve the database instance
        fun getDatabase(context: Context): FavoriteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavoriteDatabase::class.java,
                    "favorite_rocks_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
