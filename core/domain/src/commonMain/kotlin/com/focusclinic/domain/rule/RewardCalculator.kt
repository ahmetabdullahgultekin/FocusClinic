package com.focusclinic.domain.rule

import com.focusclinic.domain.valueobject.Coin
import com.focusclinic.domain.valueobject.ExperiencePoints
import com.focusclinic.domain.valueobject.FocusDuration
import com.focusclinic.domain.valueobject.Multiplier
import kotlin.math.floor

data class RewardResult(
    val xp: ExperiencePoints,
    val coins: Coin,
)

object RewardCalculator {

    fun calculateCompleted(
        duration: FocusDuration,
        xpMultiplier: Multiplier,
        coinMultiplier: Multiplier,
    ): RewardResult {
        if (!duration.isRewardEligible) return RewardResult(ExperiencePoints.ZERO, Coin.ZERO)

        val xp = floor(duration.minutes * ProgressionRules.XP_PER_MINUTE * xpMultiplier.capped.value).toLong()
        val coins = floor(duration.minutes * ProgressionRules.COINS_PER_MINUTE * coinMultiplier.capped.value).toLong()

        return RewardResult(
            xp = ExperiencePoints(xp),
            coins = Coin(coins),
        )
    }

    fun calculateInterrupted(
        actualMinutes: Int,
        plannedDuration: FocusDuration,
        xpMultiplier: Multiplier,
        coinMultiplier: Multiplier,
    ): RewardResult {
        if (actualMinutes < FocusRules.MIN_REWARD_DURATION_MINUTES) {
            return RewardResult(ExperiencePoints.ZERO, Coin.ZERO)
        }

        val ratio = actualMinutes.toDouble() / plannedDuration.minutes
        val penalty = FocusRules.INTERRUPTION_PENALTY_MULTIPLIER

        val xp = floor(
            actualMinutes * ProgressionRules.XP_PER_MINUTE * xpMultiplier.capped.value * ratio * penalty
        ).toLong()
        val coins = floor(
            actualMinutes * ProgressionRules.COINS_PER_MINUTE * coinMultiplier.capped.value * ratio * penalty
        ).toLong()

        return RewardResult(
            xp = ExperiencePoints(xp),
            coins = Coin(coins),
        )
    }
}
