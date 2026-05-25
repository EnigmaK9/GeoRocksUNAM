package com.enigma.georocks.data.remote.api

import com.enigma.georocks.data.remote.model.LocationResponseDto
import com.enigma.georocks.data.remote.model.SampleCreateRequestDto
import com.enigma.georocks.data.remote.model.SampleResponseDto
import com.enigma.georocks.data.remote.model.TokenResponseDto
import com.enigma.georocks.data.remote.model.UserCreateRequestDto
import com.enigma.georocks.data.remote.model.UserResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RockApiService {

    // Endpoint to get the list of locations
    @GET("locations/")
    suspend fun getLocations(): List<LocationResponseDto>

    // Endpoint to get the list of rock samples
    @GET("samples/")
    suspend fun getRocks(): List<SampleResponseDto>

    // Endpoint to get details of a specific sample
    @GET("samples/{id}")
    suspend fun getRockDetail(@Path("id") id: String): SampleResponseDto

    // Endpoint to create a new sample
    @POST("samples/")
    suspend fun createSample(@Body request: SampleCreateRequestDto): SampleResponseDto

    // Endpoint to update an existing sample
    @PUT("samples/{id}")
    suspend fun updateSample(
        @Path("id") id: String,
        @Body request: SampleCreateRequestDto
    ): SampleResponseDto

    // Endpoint to delete a sample
    @DELETE("samples/{id}")
    suspend fun deleteSample(@Path("id") id: String): retrofit2.Response<Unit>

    // Endpoint to login and retrieve JWT access token
    @FormUrlEncoded
    @POST("auth/token")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): TokenResponseDto

    // Endpoint to register a new user
    @POST("auth/signup")
    suspend fun signup(
        @Body request: UserCreateRequestDto
    ): UserResponseDto

    // Endpoint to get details of current active user
    @GET("auth/me")
    suspend fun getMe(): UserResponseDto
}
