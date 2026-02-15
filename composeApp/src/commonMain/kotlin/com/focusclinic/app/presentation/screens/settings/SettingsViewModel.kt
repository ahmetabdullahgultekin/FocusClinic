package com.focusclinic.app.presentation.screens.settings

import com.focusclinic.domain.model.ThemePreference
import com.focusclinic.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        loadSettings()
    }

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.SetNotifications -> setNotifications(intent.enabled)
            is SettingsIntent.SetTheme -> setTheme(intent.preference)
            SettingsIntent.ExportData -> exportData()
            SettingsIntent.DismissExportMessage -> _state.update { it.copy(exportMessage = null) }
        }
    }

    private fun loadSettings() {
        scope.launch {
            val notifications = settingsRepository.isNotificationsEnabled()
            val theme = settingsRepository.getThemePreference()
            _state.update {
                it.copy(
                    notificationsEnabled = notifications,
                    themePreference = theme,
                    isLoading = false,
                )
            }
        }
    }

    private fun setNotifications(enabled: Boolean) {
        scope.launch {
            settingsRepository.setNotificationsEnabled(enabled)
            _state.update { it.copy(notificationsEnabled = enabled) }
        }
    }

    private fun setTheme(preference: ThemePreference) {
        scope.launch {
            settingsRepository.setThemePreference(preference)
            _state.update { it.copy(themePreference = preference) }
        }
    }

    private fun exportData() {
        // Data export is a placeholder — actual file export requires platform-specific code
        // For now, we just set a success message
        _state.update { it.copy(exportMessage = "export_success") }
    }
}
