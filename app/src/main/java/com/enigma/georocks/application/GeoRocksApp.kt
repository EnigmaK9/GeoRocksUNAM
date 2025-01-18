// File path: app/src/main/java/com/enigma/georocks/application/GeoRocksApp.kt

package com.enigma.georocks.application

import android.app.Application
import androidx.room.Room
import com.enigma.georocks.data.DiskCacheManager  // <-- Make sure this import is present
import com.enigma.georocks.data.RockRepository
import com.enigma.georocks.data.db.FavoriteRepository
import com.enigma.georocks.data.db.GeoRocksDatabase
import com.enigma.georocks.data.remote.api.RockApiService
import com.enigma.georocks.data.remote.RetrofitHelper
import retrofit2.Retrofit

class GeoRocksApp : Application() {

    lateinit var repository: RockRepository
        private set

    lateinit var favoriteRepository: FavoriteRepository
        private set

    // Add DiskCacheManager
    lateinit var diskCacheManager: DiskCacheManager
        private set

    override fun onCreate() {
        super.onCreate()

        // Retrofit initialization
        val retrofit: Retrofit = RetrofitHelper().getRetrofit()
        val apiService = retrofit.create(RockApiService::class.java)

        // Room database initialization
        val database = Room.databaseBuilder(
            applicationContext,
            GeoRocksDatabase::class.java,
            "georocks_database"
        ).build()

        val favoriteRockDao = database.favoriteRockDao()

        // Repositories
        repository = RockRepository(apiService, favoriteRockDao)
        favoriteRepository = FavoriteRepository(favoriteRockDao)

        // DiskCacheManager
        diskCacheManager = DiskCacheManager(this)
    }
}
