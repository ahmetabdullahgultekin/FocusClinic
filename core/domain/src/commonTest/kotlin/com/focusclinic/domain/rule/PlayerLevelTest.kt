package com.focusclinic.domain.rule

import com.focusclinic.domain.valueobject.ExperiencePoints
import kotlin.test.Test
import kotlin.test.assertEquals

class PlayerLevelTest {

    @Test
    fun fromXp_WhenZero_ShouldReturnIntern() {
        assertEquals(PlayerLevel.INTERN, PlayerLevel.fromXp(ExperiencePoints(0)))
    }

    @Test
    fun fromXp_WhenJustBelowAssistant_ShouldReturnIntern() {
        assertEquals(PlayerLevel.INTERN, PlayerLevel.fromXp(ExperiencePoints(999)))
    }

    @Test
    fun fromXp_WhenExactlyAssistant_ShouldReturnAssistant() {
        assertEquals(PlayerLevel.ASSISTANT, PlayerLevel.fromXp(ExperiencePoints(1_000)))
    }

    @Test
    fun fromXp_WhenExactlyResident_ShouldReturnResident() {
        assertEquals(PlayerLevel.RESIDENT, PlayerLevel.fromXp(ExperiencePoints(3_000)))
    }

    @Test
    fun fromXp_WhenExactlySpecialist_ShouldReturnSpecialist() {
        assertEquals(PlayerLevel.SPECIALIST, PlayerLevel.fromXp(ExperiencePoints(10_000)))
    }

    @Test
    fun fromXp_WhenExactlyAssociateProfessor_ShouldReturnAssociateProfessor() {
        assertEquals(PlayerLevel.ASSOCIATE_PROFESSOR, PlayerLevel.fromXp(ExperiencePoints(25_000)))
    }

    @Test
    fun fromXp_WhenExactlyProfessor_ShouldReturnProfessor() {
        assertEquals(PlayerLevel.PROFESSOR, PlayerLevel.fromXp(ExperiencePoints(60_000)))
    }

    @Test
    fun fromXp_WhenFarAboveMax_ShouldReturnProfessor() {
        assertEquals(PlayerLevel.PROFESSOR, PlayerLevel.fromXp(ExperiencePoints(1_000_000)))
    }

    @Test
    fun levels_ShouldHaveCorrectNumbers() {
        assertEquals(1, PlayerLevel.INTERN.level)
        assertEquals(2, PlayerLevel.ASSISTANT.level)
        assertEquals(3, PlayerLevel.RESIDENT.level)
        assertEquals(5, PlayerLevel.SPECIALIST.level)
        assertEquals(7, PlayerLevel.ASSOCIATE_PROFESSOR.level)
        assertEquals(10, PlayerLevel.PROFESSOR.level)
    }
}
