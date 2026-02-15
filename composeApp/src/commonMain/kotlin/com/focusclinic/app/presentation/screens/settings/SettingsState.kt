package com.focusclinic.app.presentation.screens.settings

import com.focusclinic.domain.model.ThemePreference

data class SettingsState(
    val notificationsEnabled: Boolean = true,
    val themePreference: ThemePreference = ThemePreference.System,
    val isLoading: Boolean = true,
    val exportMessage: String? = null,
)
