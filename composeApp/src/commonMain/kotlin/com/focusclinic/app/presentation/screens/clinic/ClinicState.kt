package com.focusclinic.app.presentation.screens.clinic

import com.focusclinic.domain.model.InventoryItem
import com.focusclinic.domain.model.ItemType
import com.focusclinic.domain.rule.PlayerLevel
import com.focusclinic.domain.valueobject.ExperiencePoints
import com.focusclinic.domain.valueobject.Multiplier

data class ClinicState(
    val playerLevel: PlayerLevel = PlayerLevel.INTERN,
    val totalXp: ExperiencePoints = ExperiencePoints.ZERO,
    val xpMultiplier: Multiplier = Multiplier.BASE,
    val coinMultiplier: Multiplier = Multiplier.BASE,
    val equipment: List<InventoryItem> = emptyList(),
    val decorations: List<InventoryItem> = emptyList(),
) {
    val nextLevel: PlayerLevel?
        get() {
            val entries = PlayerLevel.entries
            val currentIndex = entries.indexOf(playerLevel)
            return if (currentIndex < entries.lastIndex) entries[currentIndex + 1] else null
        }

    val xpProgress: Float
        get() {
            val next = nextLevel ?: return 1f
            val currentThreshold = playerLevel.requiredXp.value
            val nextThreshold = next.requiredXp.value
            val range = nextThreshold - currentThreshold
            if (range <= 0) return 1f
            return ((totalXp.value - currentThreshold).toFloat() / range).coerceIn(0f, 1f)
        }

    val xpToNextLevel: Long
        get() {
            val next = nextLevel ?: return 0
            return (next.requiredXp.value - totalXp.value).coerceAtLeast(0)
        }
}
