package com.focusclinic.domain.valueobject

import com.focusclinic.domain.rule.FocusRules

@kotlin.jvm.JvmInline
value class FocusDuration(val minutes: Int) {

    init {
        require(minutes > 0) { "Duration must be positive: $minutes" }
    }

    val isRewardEligible: Boolean
        get() = minutes >= FocusRules.MIN_REWARD_DURATION_MINUTES

    companion object {
        val ALLOWED_DURATIONS = listOf(
            FocusDuration(5),
            FocusDuration(10),
            FocusDuration(15),
            FocusDuration(25),
            FocusDuration(45),
            FocusDuration(60),
        )
    }
}
