package com.enigma.georocks.data.remote.model

import com.google.gson.annotations.SerializedName

data class LocationResponseDto(
    @SerializedName("uid") val uid: String,
    @SerializedName("name") val name: String,
    @SerializedName("country") val country: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)
