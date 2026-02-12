package com.focusclinic.data.mapper

import com.focusclinic.data.database.Inventory
import com.focusclinic.domain.model.InventoryItem
import com.focusclinic.domain.model.ItemType

fun Inventory.toDomain(): InventoryItem = InventoryItem(
    itemId = item_id,
    name = item_name,
    type = if (item_type == "EQUIPMENT") ItemType.EQUIPMENT else ItemType.DECORATION,
    modifier = toModifierType(modifier_type, modifier_value),
    purchasedAt = purchased_at,
)
