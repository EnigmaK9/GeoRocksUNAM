package com.enigma.georocks.data.remote.model

import com.google.gson.annotations.SerializedName

data class RockDetailDto(
    @SerializedName("title")
    var title: String? = null,

    @SerializedName("image")
    var image: String? = null,

    @SerializedName("video")
    var video: String? = null,

    @SerializedName("long_desc")
    var longDesc: String? = null,

    @SerializedName("a_member_of")
    var aMemberOf: String? = null,

    @SerializedName("also_known_as")
    var alsoKnownAs: List<String>? = null,

    @SerializedName("formula")
    var formula: String? = null,

    @SerializedName("hardness")
    var hardness: Int? = null,

    @SerializedName("color")
    var color: String? = null,

    @SerializedName("magnetic")
    var magnetic: Boolean? = null,

    @SerializedName("health_risks")
    var healthRisks: String? = null,

    @SerializedName("latitude")
    var latitude: Double? = null,

    @SerializedName("longitude")
    var longitude: Double? = null,

    @SerializedName("images")
    var images: List<String>? = null,

    @SerializedName("localities")
    var localities: List<String>? = null,

    @SerializedName("frequently_asked_questions")
    var frequentlyAskedQuestions: List<String>? = null,

    @SerializedName("physical_properties")
    var physicalProperties: PhysicalProperties? = null,

    @SerializedName("chemical_properties")
    var chemicalProperties: ChemicalProperties? = null
) {
    data class PhysicalProperties(
        @SerializedName("pp_crystal_system")
        var ppCrystalSystem: String? = null,

        @SerializedName("pp_colors")
        var ppColors: List<String>? = null,

        @SerializedName("pp_luster")
        var ppLuster: String? = null,

        @SerializedName("pp_diaphaneity")
        var ppDiaphaneity: String? = null,

        @SerializedName("pp_magnetic")
        var ppMagnetic: Boolean? = null,

        @SerializedName("pp_streak")
        var ppStreak: String? = null,

        @SerializedName("pp_hardness")
        var ppHardness: Int? = null,

        @SerializedName("pp_tenacity")
        var ppTenacity: String? = null,

        @SerializedName("pp_cleavage")
        var ppCleavage: String? = null,

        @SerializedName("pp_fracture")
        var ppFracture: String? = null,

        @SerializedName("pp_density")
        var ppDensity: String? = null
    )

    data class ChemicalProperties(
        @SerializedName("cp_chemical_classification")
        var cpChemicalClassification: String? = null,

        @SerializedName("cp_formula")
        var cpFormula: String? = null,

        @SerializedName("cp_elements_listed")
        var cpElementsListed: List<String>? = null,

        @SerializedName("cp_common_impurities")
        var cpCommonImpurities: List<String>? = null
    )
}
