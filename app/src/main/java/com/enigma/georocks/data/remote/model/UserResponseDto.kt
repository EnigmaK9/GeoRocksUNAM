package com.enigma.georocks.data.remote.model

import com.google.gson.annotations.SerializedName

data class UserResponseDto(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("is_active") val isActive: Boolean
)
