package com.focusclinic.domain.repository

import com.focusclinic.domain.model.ThemePreference

interface SettingsRepository {
    suspend fun isOnboardingCompleted(): Boolean
    suspend fun markOnboardingCompleted()
    suspend fun getThemePreference(): ThemePreference
    suspend fun setThemePreference(preference: ThemePreference)
    suspend fun isNotificationsEnabled(): Boolean
    suspend fun setNotificationsEnabled(enabled: Boolean)
}
