package com.focusclinic.domain.repository

import com.focusclinic.domain.model.UserProfile
import com.focusclinic.domain.valueobject.ExperiencePoints
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun observeProfile(): Flow<UserProfile>
    suspend fun getProfile(): UserProfile
    suspend fun addXp(xp: ExperiencePoints)
    suspend fun ensureProfileExists()
}
