package com.focusclinic.data.mapper

import com.focusclinic.domain.model.ModifierType

private const val MODIFIER_XP_BONUS = "XP_BONUS"
private const val MODIFIER_COIN_BONUS = "COIN_BONUS"
private const val MODIFIER_NONE = "NONE"

fun ModifierType.toDbString(): String = when (this) {
    is ModifierType.XpBonus -> MODIFIER_XP_BONUS
    is ModifierType.CoinBonus -> MODIFIER_COIN_BONUS
    ModifierType.None -> MODIFIER_NONE
}

fun toModifierType(dbString: String?, value: Double): ModifierType = when (dbString) {
    MODIFIER_XP_BONUS -> ModifierType.XpBonus(value)
    MODIFIER_COIN_BONUS -> ModifierType.CoinBonus(value)
    else -> ModifierType.None
}
