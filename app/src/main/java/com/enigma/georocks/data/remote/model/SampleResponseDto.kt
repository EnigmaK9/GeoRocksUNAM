package com.enigma.georocks.data.remote.model

import com.google.gson.annotations.SerializedName

data class SampleResponseDto(
    @SerializedName("uid") val uid: String,
    @SerializedName("cut") val cut: Boolean,
    @SerializedName("thin_section") val thinSection: Boolean,
    @SerializedName("picture") val picture: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("rock_name") val rockName: String,
    @SerializedName("rock_description") val rockDescription: String?,
    @SerializedName("location_name") val locationName: String,
    @SerializedName("location_country") val locationCountry: String
)
