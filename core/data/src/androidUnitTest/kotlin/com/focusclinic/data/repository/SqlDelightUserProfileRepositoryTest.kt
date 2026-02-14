package com.focusclinic.data.repository

import com.focusclinic.data.TestDatabaseFactory
import com.focusclinic.domain.rule.PlayerLevel
import com.focusclinic.domain.valueobject.ExperiencePoints
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SqlDelightUserProfileRepositoryTest {

    private val database = TestDatabaseFactory.create()
    private val repository = SqlDelightUserProfileRepository(
        database = database,
        clock = { 1000L },
    )

    private fun seedProfile() {
        database.userProfileQueries.insertProfile(created_at = 1000L)
    }

    @Test
    fun getProfile_ShouldReturnDefaultProfile() = runTest {
        seedProfile()
        val profile = repository.getProfile()

        assertEquals(ExperiencePoints.ZERO, profile.totalXp)
        assertEquals(PlayerLevel.BEGINNER, profile.level)
    }

    @Test
    fun addXp_ShouldIncrementTotalXp() = runTest {
        seedProfile()
        repository.addXp(ExperiencePoints(500))

        val profile = repository.getProfile()
        assertEquals(ExperiencePoints(500), profile.totalXp)
    }

    @Test
    fun addXp_WhenCrossingThreshold_ShouldUpdateLevel() = runTest {
        seedProfile()
        repository.addXp(ExperiencePoints(1000))

        val profile = repository.getProfile()
        assertEquals(PlayerLevel.APPRENTICE, profile.level)
    }

    @Test
    fun addXp_MultipleTimes_ShouldAccumulate() = runTest {
        seedProfile()
        repository.addXp(ExperiencePoints(500))
        repository.addXp(ExperiencePoints(600))

        val profile = repository.getProfile()
        assertEquals(ExperiencePoints(1100), profile.totalXp)
        assertEquals(PlayerLevel.APPRENTICE, profile.level)
    }

    @Test
    fun observeProfile_ShouldEmitUpdates() = runTest {
        seedProfile()

        val initial = repository.observeProfile().first()
        assertEquals(ExperiencePoints.ZERO, initial.totalXp)
    }

    @Test
    fun ensureProfileExists_ShouldNotOverwriteExisting() = runTest {
        seedProfile()
        repository.addXp(ExperiencePoints(500))
        repository.ensureProfileExists()

        val profile = repository.getProfile()
        assertEquals(ExperiencePoints(500), profile.totalXp)
    }
}
