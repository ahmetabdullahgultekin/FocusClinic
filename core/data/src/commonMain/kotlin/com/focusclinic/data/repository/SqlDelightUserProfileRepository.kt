package com.focusclinic.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import com.focusclinic.data.database.FocusClinicDatabase
import com.focusclinic.data.mapper.toDomain
import com.focusclinic.domain.model.UserProfile
import com.focusclinic.domain.repository.UserProfileRepository
import com.focusclinic.domain.rule.PlayerLevel
import com.focusclinic.domain.valueobject.ExperiencePoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SqlDelightUserProfileRepository(
    private val database: FocusClinicDatabase,
    private val clock: () -> Long,
) : UserProfileRepository {

    private val queries get() = database.userProfileQueries

    override fun observeProfile(): Flow<UserProfile> =
        queries.getProfile()
            .asFlow()
            .mapToOne(Dispatchers.Default)
            .map { it.toDomain() }

    override suspend fun getProfile(): UserProfile =
        queries.getProfile().executeAsOne().toDomain()

    override suspend fun addXp(xp: ExperiencePoints) {
        val current = queries.getProfile().executeAsOne()
        val newXp = ExperiencePoints(current.current_xp + xp.value)
        val newLevel = PlayerLevel.fromXp(newXp)
        queries.addXp(
            current_xp = xp.value,
            current_level = newLevel.level.toLong(),
            title_rank = newLevel.title,
        )
    }

    override suspend fun ensureProfileExists() {
        queries.insertProfile(created_at = clock())
    }
}
