// /home/enigma/github/kotlin/georocksunam/app/src/main/java/com/enigma/georocks/data/remote/RocksApi.kt

package com.enigma.georocks.data.remote

import com.enigma.georocks.data.remote.model.RockDetailDto
import com.enigma.georocks.data.remote.model.RockDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface RocksApi {

    // Endpoint to get the list of rocks
    @GET("rocks/rock_list")
    fun getRocksApiary(): Call<MutableList<RockDto>>

    // Endpoint to get the details of a specific rock
    @GET("rocks/rock_detail/{id}")
    fun getRockDetailApiary(
        @Path("id") id: String
    ): Call<RockDetailDto>
}
