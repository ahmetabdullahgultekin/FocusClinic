package com.focusclinic.domain.rule

import com.focusclinic.domain.model.InventoryItem
import com.focusclinic.domain.model.ModifierType
import com.focusclinic.domain.valueobject.Multiplier

object MultiplierCalculator {

    fun computeXpMultiplier(inventory: List<InventoryItem>): Multiplier {
        val totalBonus = inventory
            .map { it.modifier }
            .filterIsInstance<ModifierType.XpBonus>()
            .sumOf { it.bonusValue }

        return Multiplier(ProgressionRules.BASE_MULTIPLIER + totalBonus).capped
    }

    fun computeCoinMultiplier(inventory: List<InventoryItem>): Multiplier {
        val totalBonus = inventory
            .map { it.modifier }
            .filterIsInstance<ModifierType.CoinBonus>()
            .sumOf { it.bonusValue }

        return Multiplier(ProgressionRules.BASE_MULTIPLIER + totalBonus).capped
    }
}
