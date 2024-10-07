package com.amaurypm.videogamesrf.data

import com.amaurypm.videogamesrf.data.remote.RocksApi
import com.amaurypm.videogamesrf.data.remote.model.RockDetailDto
import com.amaurypm.videogamesrf.data.remote.model.RockDto
import retrofit2.Call
import retrofit2.Retrofit

class RockRepository(private val retrofit: Retrofit) {

    private val rocksApi: RocksApi = retrofit.create(RocksApi::class.java)

    // Método para obtener la lista de rocas
    fun getRocksApiary(): Call<MutableList<RockDto>> = rocksApi.getRocksApiary()

    // Método para obtener el detalle de una roca específica
    fun getRockDetail(id: String): Call<RockDetailDto> = rocksApi.getRockDetailApiary(id)
}
