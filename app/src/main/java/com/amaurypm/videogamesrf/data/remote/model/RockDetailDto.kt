package com.amaurypm.videogamesrf.data.remote.model

import com.google.gson.annotations.SerializedName

data class RockDetailDto(
    @SerializedName("title")
    var title: String? = null,

    @SerializedName("image")
    var image: String? = null,

    @SerializedName("long_desc")
    var longDesc: String? = null,

    @SerializedName("a_member_of")
    var aMemberOf: String? = null,

    @SerializedName("color")
    var color: String? = null,

    @SerializedName("images")
    var images: List<String>? = null,

    @SerializedName("localities")
    var localities: List<String>? = null,

    @SerializedName("physical_properties")
    var physicalProperties: PhysicalProperties? = null,

    @SerializedName("chemical_properties")
    var chemicalProperties: ChemicalProperties? = null
) {
    data class PhysicalProperties(
        @SerializedName("crystal_system")
        var crystalSystem: String? = null,

        @SerializedName("pp_colors")
        var colors: List<String>? = null,

        @SerializedName("pp_hardness")
        var hardness: Double? = null // Cambiado a Double
    )

    data class ChemicalProperties(
        @SerializedName("chemical_classification")
        var classification: String? = null,

        @SerializedName("cp_formula")
        var formula: String? = null
    )
}
