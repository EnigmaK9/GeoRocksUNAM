package com.enigma.georocks.data

import com.enigma.georocks.data.remote.RocksApi
import com.enigma.georocks.data.remote.model.RockDetailDto
import com.enigma.georocks.data.remote.model.RockDto
import retrofit2.Call
import retrofit2.Retrofit

class RockRepository(private val retrofit: Retrofit) {

    private val rocksApi: RocksApi = retrofit.create(RocksApi::class.java)

    // Method to fetch the list of rocks
    fun getRocksApiary(): Call<MutableList<RockDto>> = rocksApi.getRocksApiary()

    // Method to fetch the details of a specific rock
    fun getRockDetail(id: String): Call<RockDetailDto> = rocksApi.getRockDetailApiary(id)
}
