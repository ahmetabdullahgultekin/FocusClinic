package com.focusclinic.domain.valueobject

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MultiplierTest {

    @Test
    fun create_WhenAtBase_ShouldSucceed() {
        assertEquals(1.0, Multiplier.BASE.value)
    }

    @Test
    fun create_WhenBelowBase_ShouldThrow() {
        assertFailsWith<IllegalArgumentException> { Multiplier(0.5) }
    }

    @Test
    fun capped_WhenWithinLimit_ShouldReturnSelf() {
        val m = Multiplier(2.5)
        assertEquals(2.5, m.capped.value)
    }

    @Test
    fun capped_WhenAboveLimit_ShouldReturnCap() {
        val m = Multiplier(3.5)
        assertEquals(3.0, m.capped.value)
    }

    @Test
    fun plus_WhenWithinCap_ShouldAdd() {
        val result = Multiplier(1.0) + 0.5
        assertEquals(1.5, result.value)
    }

    @Test
    fun plus_WhenExceedsCap_ShouldCoerce() {
        val result = Multiplier(2.5) + 1.0
        assertEquals(3.0, result.value)
    }
}
