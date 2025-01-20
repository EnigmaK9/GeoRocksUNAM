package com.enigma.georocks.di

import android.content.Context
import androidx.room.Room
import com.enigma.georocks.data.DiskCacheManager
import com.enigma.georocks.data.RockRepository
import com.enigma.georocks.data.db.FavoriteRockDao
import com.enigma.georocks.data.db.FavoriteRepository
import com.enigma.georocks.data.db.GeoRocksDatabase
import com.enigma.georocks.data.remote.RetrofitHelper
import com.enigma.georocks.data.remote.api.RockApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Provide Retrofit instance
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = RetrofitHelper().getRetrofit()

    // Provide RockApiService instance
    @Provides
    @Singleton
    fun provideRockApiService(retrofit: Retrofit): RockApiService =
        retrofit.create(RockApiService::class.java)

    // Provide Room Database instance
    @Provides
    @Singleton
    fun provideGeoRocksDatabase(@ApplicationContext app: Context): GeoRocksDatabase =
        Room.databaseBuilder(
            app,
            GeoRocksDatabase::class.java,
            "georocks_database"
        ).fallbackToDestructiveMigration().build()

    // Provide FavoriteRockDao instance
    @Provides
    @Singleton
    fun provideFavoriteRockDao(db: GeoRocksDatabase): FavoriteRockDao =
        db.favoriteRockDao()

    // Provide RockRepository instance
    @Provides
    @Singleton
    fun provideRockRepository(apiService: RockApiService, dao: FavoriteRockDao): RockRepository =
        RockRepository(apiService, dao)

    // Provide FavoriteRepository instance
    @Provides
    @Singleton
    fun provideFavoriteRepository(dao: FavoriteRockDao): FavoriteRepository =
        FavoriteRepository(dao)

    // Provide DiskCacheManager instance
    @Provides
    @Singleton
    fun provideDiskCacheManager(@ApplicationContext app: Context): DiskCacheManager =
        DiskCacheManager(app)
}
