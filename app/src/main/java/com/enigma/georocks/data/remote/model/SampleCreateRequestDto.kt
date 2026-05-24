package com.enigma.georocks.data.remote.model

import com.google.gson.annotations.SerializedName

data class SampleCreateRequestDto(
    @SerializedName("rock_name") val rockName: String,
    @SerializedName("description") val description: String,
    @SerializedName("location_name") val locationName: String,
    @SerializedName("location_country") val locationCountry: String,
    @SerializedName("cut") val cut: Boolean,
    @SerializedName("thin_section") val thinSection: Boolean,
    @SerializedName("picture") val picture: String
)
