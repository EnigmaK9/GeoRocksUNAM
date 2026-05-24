package com.enigma.georocks.data.remote.model

import com.google.gson.annotations.SerializedName

data class TokenResponseDto(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String
)
