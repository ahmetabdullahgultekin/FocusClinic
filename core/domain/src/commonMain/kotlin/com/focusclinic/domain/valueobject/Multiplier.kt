package com.focusclinic.domain.valueobject

import com.focusclinic.domain.rule.ProgressionRules

@kotlin.jvm.JvmInline
value class Multiplier(val value: Double) {

    init {
        require(value >= ProgressionRules.BASE_MULTIPLIER) {
            "Multiplier cannot be below base (${ProgressionRules.BASE_MULTIPLIER}): $value"
        }
    }

    val capped: Multiplier
        get() = if (value > ProgressionRules.MAX_MULTIPLIER_CAP) {
            Multiplier(ProgressionRules.MAX_MULTIPLIER_CAP)
        } else {
            this
        }

    operator fun plus(bonus: Double): Multiplier =
        Multiplier((value + bonus).coerceAtMost(ProgressionRules.MAX_MULTIPLIER_CAP))

    companion object {
        val BASE = Multiplier(ProgressionRules.BASE_MULTIPLIER)
    }
}
