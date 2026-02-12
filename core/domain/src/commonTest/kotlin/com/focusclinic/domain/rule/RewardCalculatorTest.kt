package com.focusclinic.domain.rule

import com.focusclinic.domain.valueobject.FocusDuration
import com.focusclinic.domain.valueobject.Multiplier
import kotlin.test.Test
import kotlin.test.assertEquals

class RewardCalculatorTest {

    // ── calculateCompleted ──

    @Test
    fun calculateCompleted_WhenBaseMultiplier5min_ShouldReturn50xp25coins() {
        val result = RewardCalculator.calculateCompleted(
            duration = FocusDuration(5),
            xpMultiplier = Multiplier.BASE,
            coinMultiplier = Multiplier.BASE,
        )
        // 5 * 10 * 1.0 = 50 XP, 5 * 5 * 1.0 = 25 coins
        assertEquals(50, result.xp.value)
        assertEquals(25, result.coins.amount)
    }

    @Test
    fun calculateCompleted_WhenBaseMultiplier25min_ShouldReturn250xp125coins() {
        val result = RewardCalculator.calculateCompleted(
            duration = FocusDuration(25),
            xpMultiplier = Multiplier.BASE,
            coinMultiplier = Multiplier.BASE,
        )
        assertEquals(250, result.xp.value)
        assertEquals(125, result.coins.amount)
    }

    @Test
    fun calculateCompleted_WhenDoubleXpMultiplier_ShouldDoubleXp() {
        val result = RewardCalculator.calculateCompleted(
            duration = FocusDuration(10),
            xpMultiplier = Multiplier(2.0),
            coinMultiplier = Multiplier.BASE,
        )
        // 10 * 10 * 2.0 = 200 XP, 10 * 5 * 1.0 = 50 coins
        assertEquals(200, result.xp.value)
        assertEquals(50, result.coins.amount)
    }

    @Test
    fun calculateCompleted_WhenDoubleCoinMultiplier_ShouldDoubleCoins() {
        val result = RewardCalculator.calculateCompleted(
            duration = FocusDuration(10),
            xpMultiplier = Multiplier.BASE,
            coinMultiplier = Multiplier(2.0),
        )
        assertEquals(100, result.xp.value)
        assertEquals(100, result.coins.amount)
    }

    @Test
    fun calculateCompleted_WhenBelowMinDuration_ShouldReturnZero() {
        val result = RewardCalculator.calculateCompleted(
            duration = FocusDuration(4),
            xpMultiplier = Multiplier.BASE,
            coinMultiplier = Multiplier.BASE,
        )
        assertEquals(0, result.xp.value)
        assertEquals(0, result.coins.amount)
    }

    // ── calculateInterrupted ──

    @Test
    fun calculateInterrupted_WhenBelowMinDuration_ShouldReturnZero() {
        val result = RewardCalculator.calculateInterrupted(
            actualMinutes = 3,
            plannedDuration = FocusDuration(25),
            xpMultiplier = Multiplier.BASE,
            coinMultiplier = Multiplier.BASE,
        )
        assertEquals(0, result.xp.value)
        assertEquals(0, result.coins.amount)
    }

    @Test
    fun calculateInterrupted_WhenHalfCompleted_ShouldApplyRatioAndPenalty() {
        val result = RewardCalculator.calculateInterrupted(
            actualMinutes = 5,
            plannedDuration = FocusDuration(10),
            xpMultiplier = Multiplier.BASE,
            coinMultiplier = Multiplier.BASE,
        )
        // 5 * 10 * 1.0 * (5/10) * 0.5 = 12.5 → 12
        // 5 * 5 * 1.0 * (5/10) * 0.5 = 6.25 → 6
        assertEquals(12, result.xp.value)
        assertEquals(6, result.coins.amount)
    }

    @Test
    fun calculateInterrupted_When70PercentCompleted_ShouldApplyRatioAndPenalty() {
        val result = RewardCalculator.calculateInterrupted(
            actualMinutes = 7,
            plannedDuration = FocusDuration(10),
            xpMultiplier = Multiplier.BASE,
            coinMultiplier = Multiplier.BASE,
        )
        // 7 * 10 * 1.0 * (7/10) * 0.5 = 24.5 → 24
        // 7 * 5 * 1.0 * (7/10) * 0.5 = 12.25 → 12
        assertEquals(24, result.xp.value)
        assertEquals(12, result.coins.amount)
    }

    @Test
    fun calculateInterrupted_WhenFullyCompleted_ShouldStillApplyPenalty() {
        val result = RewardCalculator.calculateInterrupted(
            actualMinutes = 10,
            plannedDuration = FocusDuration(10),
            xpMultiplier = Multiplier.BASE,
            coinMultiplier = Multiplier.BASE,
        )
        // 10 * 10 * 1.0 * (10/10) * 0.5 = 50
        // 10 * 5 * 1.0 * (10/10) * 0.5 = 25
        assertEquals(50, result.xp.value)
        assertEquals(25, result.coins.amount)
    }
}
