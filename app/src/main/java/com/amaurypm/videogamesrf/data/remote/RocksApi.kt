package com.amaurypm.videogamesrf.data.remote

import com.amaurypm.videogamesrf.data.remote.model.RockDetailDto
import com.amaurypm.videogamesrf.data.remote.model.RockDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface RocksApi {

    // Endpoint para obtener la lista de rocas
    @GET("rocks/rock_list")
    fun getRocksApiary(): Call<MutableList<RockDto>>

    // Endpoint para obtener el detalle de una roca específica
    @GET("rocks/rock_detail/{id}")
    fun getRockDetailApiary(
        @Path("id") id: String
    ): Call<RockDetailDto>
}
