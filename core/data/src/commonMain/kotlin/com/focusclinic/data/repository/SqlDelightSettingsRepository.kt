package com.focusclinic.data.repository

import com.focusclinic.data.database.FocusClinicDatabase
import com.focusclinic.domain.model.ThemePreference
import com.focusclinic.domain.repository.SettingsRepository

class SqlDelightSettingsRepository(
    private val database: FocusClinicDatabase,
) : SettingsRepository {

    private val queries get() = database.userProfileQueries

    override suspend fun isOnboardingCompleted(): Boolean {
        val result = queries.isOnboardingCompleted().executeAsOneOrNull()
        return result == 1L
    }

    override suspend fun markOnboardingCompleted() {
        queries.markOnboardingCompleted()
    }

    override suspend fun getThemePreference(): ThemePreference {
        val value = queries.getThemePreference().executeAsOneOrNull() ?: "SYSTEM"
        return ThemePreference.fromDbValue(value)
    }

    override suspend fun setThemePreference(preference: ThemePreference) {
        queries.updateThemePreference(preference.dbValue)
    }

    override suspend fun isNotificationsEnabled(): Boolean {
        val result = queries.getNotificationsEnabled().executeAsOneOrNull()
        return result != 0L
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        queries.updateNotificationsEnabled(if (enabled) 1L else 0L)
    }
}
