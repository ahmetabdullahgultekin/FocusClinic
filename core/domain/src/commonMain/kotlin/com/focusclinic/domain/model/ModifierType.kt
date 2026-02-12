package com.focusclinic.domain.model

sealed interface ModifierType {
    val bonusValue: Double

    data class XpBonus(override val bonusValue: Double) : ModifierType
    data class CoinBonus(override val bonusValue: Double) : ModifierType
    data object None : ModifierType {
        override val bonusValue: Double = 0.0
    }
}
