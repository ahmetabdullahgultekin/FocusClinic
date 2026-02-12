package com.focusclinic.domain.model

data class ClinicAttributes(
    val wallTheme: String,
    val chairType: String,
    val equipmentSet: List<String>,
    val decorations: List<String>,
) {
    companion object {
        val DEFAULT = ClinicAttributes(
            wallTheme = "default",
            chairType = "basic",
            equipmentSet = emptyList(),
            decorations = emptyList(),
        )
    }
}
