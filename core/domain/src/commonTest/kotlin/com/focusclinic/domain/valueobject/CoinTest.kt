package com.focusclinic.domain.valueobject

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class CoinTest {

    @Test
    fun create_WhenValidAmount_ShouldSucceed() {
        val coin = Coin(100)
        assertEquals(100, coin.amount)
    }

    @Test
    fun create_WhenZero_ShouldSucceed() {
        assertEquals(0, Coin.ZERO.amount)
    }

    @Test
    fun create_WhenNegative_ShouldThrow() {
        assertFailsWith<IllegalArgumentException> { Coin(-1) }
    }

    @Test
    fun plus_ShouldAddAmounts() {
        val result = Coin(30) + Coin(20)
        assertEquals(50, result.amount)
    }

    @Test
    fun minus_WhenSufficient_ShouldSubtract() {
        val result = Coin(50) - Coin(20)
        assertEquals(30, result.amount)
    }

    @Test
    fun minus_WhenInsufficient_ShouldThrow() {
        assertFailsWith<IllegalArgumentException> { Coin(10) - Coin(20) }
    }

    @Test
    fun compareTo_ShouldCompareAmounts() {
        assertTrue(Coin(100).compareTo(Coin(50)) > 0)
        assertTrue(Coin(50).compareTo(Coin(100)) < 0)
        assertEquals(0, Coin(50).compareTo(Coin(50)))
    }
}
