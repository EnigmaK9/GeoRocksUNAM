///home/enigma/github/kotlin/georocksunam/app/src/main/java/com/enigma/georocks/data/remote/api/RockApiService.kt
package com.enigma.georocks.data.remote.api

import com.enigma.georocks.data.remote.model.RockDetailDto
import com.enigma.georocks.data.remote.model.RockDto
import retrofit2.http.GET
import retrofit2.http.Path

interface RockApiService {

    // Endpoint Rock list
    @GET("rocks/rock_list")
    suspend fun getRocks(): MutableList<RockDto>

    // Endpoint rock detail
    @GET("rocks/rock_detail/{id}")
    suspend fun getRockDetail(@Path("id") id: String): RockDetailDto
}
