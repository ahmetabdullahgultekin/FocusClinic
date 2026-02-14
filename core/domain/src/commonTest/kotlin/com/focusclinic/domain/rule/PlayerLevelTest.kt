package com.focusclinic.domain.rule

import com.focusclinic.domain.valueobject.ExperiencePoints
import kotlin.test.Test
import kotlin.test.assertEquals

class PlayerLevelTest {

    @Test
    fun fromXp_WhenZero_ShouldReturnBeginner() {
        assertEquals(PlayerLevel.BEGINNER, PlayerLevel.fromXp(ExperiencePoints(0)))
    }

    @Test
    fun fromXp_WhenJustBelowApprentice_ShouldReturnBeginner() {
        assertEquals(PlayerLevel.BEGINNER, PlayerLevel.fromXp(ExperiencePoints(999)))
    }

    @Test
    fun fromXp_WhenExactlyApprentice_ShouldReturnApprentice() {
        assertEquals(PlayerLevel.APPRENTICE, PlayerLevel.fromXp(ExperiencePoints(1_000)))
    }

    @Test
    fun fromXp_WhenExactlyDetermined_ShouldReturnDetermined() {
        assertEquals(PlayerLevel.DETERMINED, PlayerLevel.fromXp(ExperiencePoints(3_000)))
    }

    @Test
    fun fromXp_WhenExactlyStrong_ShouldReturnStrong() {
        assertEquals(PlayerLevel.STRONG, PlayerLevel.fromXp(ExperiencePoints(10_000)))
    }

    @Test
    fun fromXp_WhenExactlyMaster_ShouldReturnMaster() {
        assertEquals(PlayerLevel.MASTER, PlayerLevel.fromXp(ExperiencePoints(25_000)))
    }

    @Test
    fun fromXp_WhenExactlyLegend_ShouldReturnLegend() {
        assertEquals(PlayerLevel.LEGEND, PlayerLevel.fromXp(ExperiencePoints(60_000)))
    }

    @Test
    fun fromXp_WhenFarAboveMax_ShouldReturnLegend() {
        assertEquals(PlayerLevel.LEGEND, PlayerLevel.fromXp(ExperiencePoints(1_000_000)))
    }

    @Test
    fun levels_ShouldHaveCorrectNumbers() {
        assertEquals(1, PlayerLevel.BEGINNER.level)
        assertEquals(2, PlayerLevel.APPRENTICE.level)
        assertEquals(3, PlayerLevel.DETERMINED.level)
        assertEquals(5, PlayerLevel.STRONG.level)
        assertEquals(7, PlayerLevel.MASTER.level)
        assertEquals(10, PlayerLevel.LEGEND.level)
    }
}
