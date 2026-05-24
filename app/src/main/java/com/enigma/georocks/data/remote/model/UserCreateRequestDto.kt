package com.enigma.georocks.data.remote.model

import com.google.gson.annotations.SerializedName

data class UserCreateRequestDto(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String
)
