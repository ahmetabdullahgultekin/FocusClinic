package com.focusclinic.domain.valueobject

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FocusDurationTest {

    @Test
    fun create_WhenPositive_ShouldSucceed() {
        assertEquals(25, FocusDuration(25).minutes)
    }

    @Test
    fun create_WhenZero_ShouldThrow() {
        assertFailsWith<IllegalArgumentException> { FocusDuration(0) }
    }

    @Test
    fun create_WhenNegative_ShouldThrow() {
        assertFailsWith<IllegalArgumentException> { FocusDuration(-5) }
    }

    @Test
    fun isRewardEligible_WhenAtMinimum_ShouldReturnTrue() {
        assertTrue(FocusDuration(5).isRewardEligible)
    }

    @Test
    fun isRewardEligible_WhenAboveMinimum_ShouldReturnTrue() {
        assertTrue(FocusDuration(25).isRewardEligible)
    }

    @Test
    fun isRewardEligible_WhenBelowMinimum_ShouldReturnFalse() {
        assertFalse(FocusDuration(4).isRewardEligible)
    }

    @Test
    fun allowedDurations_ShouldContainSixOptions() {
        assertEquals(6, FocusDuration.ALLOWED_DURATIONS.size)
    }

    @Test
    fun allowedDurations_ShouldContainExpectedValues() {
        val expected = listOf(5, 10, 15, 25, 45, 60)
        assertEquals(expected, FocusDuration.ALLOWED_DURATIONS.map { it.minutes })
    }
}
