package com.focusclinic.app.presentation.screens.settings

import com.focusclinic.domain.model.ThemePreference

sealed interface SettingsIntent {
    data class SetNotifications(val enabled: Boolean) : SettingsIntent
    data class SetTheme(val preference: ThemePreference) : SettingsIntent
    data object ExportData : SettingsIntent
    data object DismissExportMessage : SettingsIntent
}
