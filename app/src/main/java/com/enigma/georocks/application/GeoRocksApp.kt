// File path: app/src/main/java/com/enigma/georocks/application/GeoRocksApp.kt

package com.enigma.georocks.application

import android.app.Application
import androidx.room.Room
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

    override fun onCreate() {
        super.onCreate()

        // Retrofit is initialized
        val retrofit: Retrofit = RetrofitHelper().getRetrofit()
        val apiService = retrofit.create(RockApiService::class.java)

        // Room database is initialized
        val database = Room.databaseBuilder(
            applicationContext,
            GeoRocksDatabase::class.java,
            "georocks_database"
        ).build()

        val favoriteRockDao = database.favoriteRockDao()

        // Repositories are initialized
        repository = RockRepository(apiService, favoriteRockDao)
        favoriteRepository = FavoriteRepository(favoriteRockDao)
    }
}
