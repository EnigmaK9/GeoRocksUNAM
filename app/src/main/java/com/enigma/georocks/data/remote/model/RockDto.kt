
package com.enigma.georocks.data.remote.model
import com.google.gson.annotations.SerializedName

data class RockDto(
    @SerializedName("id") var id: String,
    @SerializedName("thumbnail") var thumbnail: String?,
    @SerializedName("title") var title: String
)
