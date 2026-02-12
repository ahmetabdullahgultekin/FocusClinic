package com.focusclinic.domain.valueobject

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ExperiencePointsTest {

    @Test
    fun create_WhenValid_ShouldSucceed() {
        assertEquals(100, ExperiencePoints(100).value)
    }

    @Test
    fun create_WhenZero_ShouldSucceed() {
        assertEquals(0, ExperiencePoints.ZERO.value)
    }

    @Test
    fun create_WhenNegative_ShouldThrow() {
        assertFailsWith<IllegalArgumentException> { ExperiencePoints(-1) }
    }

    @Test
    fun plus_ShouldAddValues() {
        val result = ExperiencePoints(30) + ExperiencePoints(70)
        assertEquals(100, result.value)
    }

    @Test
    fun compareTo_ShouldCompareValues() {
        assertTrue(ExperiencePoints(100).compareTo(ExperiencePoints(50)) > 0)
        assertTrue(ExperiencePoints(50).compareTo(ExperiencePoints(100)) < 0)
        assertEquals(0, ExperiencePoints(50).compareTo(ExperiencePoints(50)))
    }
}
