package com.focusclinic.domain.repository

import com.focusclinic.domain.model.InventoryItem
import kotlinx.coroutines.flow.Flow

interface InventoryRepository {
    fun observeInventory(): Flow<List<InventoryItem>>
    suspend fun getAll(): List<InventoryItem>
    suspend fun add(item: InventoryItem)
    suspend fun exists(itemId: String): Boolean
}
