package com.focusclinic.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.focusclinic.data.database.FocusClinicDatabase
import com.focusclinic.data.mapper.toDomain
import com.focusclinic.data.mapper.toDbString
import com.focusclinic.domain.model.InventoryItem
import com.focusclinic.domain.repository.InventoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SqlDelightInventoryRepository(
    private val database: FocusClinicDatabase,
) : InventoryRepository {

    private val queries get() = database.inventoryQueries

    override fun observeInventory(): Flow<List<InventoryItem>> =
        queries.getAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list -> list.map { it.toDomain() } }

    override suspend fun getAll(): List<InventoryItem> =
        queries.getAll().executeAsList().map { it.toDomain() }

    override suspend fun add(item: InventoryItem) {
        queries.insert(
            item_id = item.itemId,
            item_name = item.name,
            item_type = item.type.name,
            modifier_type = item.modifier.toDbString(),
            modifier_value = item.modifier.bonusValue,
            purchased_at = item.purchasedAt,
        )
    }

    override suspend fun exists(itemId: String): Boolean =
        queries.existsById(itemId).executeAsOne() > 0
}
