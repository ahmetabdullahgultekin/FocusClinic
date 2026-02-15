package com.focusclinic.domain.model

sealed interface ThemePreference {
    data object System : ThemePreference
    data object Light : ThemePreference
    data object Dark : ThemePreference

    val dbValue: String
        get() = when (this) {
            System -> DB_SYSTEM
            Light -> DB_LIGHT
            Dark -> DB_DARK
        }

    companion object {
        private const val DB_SYSTEM = "SYSTEM"
        private const val DB_LIGHT = "LIGHT"
        private const val DB_DARK = "DARK"

        fun fromDbValue(value: String): ThemePreference = when (value) {
            DB_LIGHT -> Light
            DB_DARK -> Dark
            else -> System
        }
    }
}
