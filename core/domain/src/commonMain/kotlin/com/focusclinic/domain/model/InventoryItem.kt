package com.focusclinic.domain.model

data class InventoryItem(
    val itemId: String,
    val name: String,
    val type: ItemType,
    val modifier: ModifierType,
    val purchasedAt: Long,
)
