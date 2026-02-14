package com.focusclinic.data.repository

import com.focusclinic.data.TestDatabaseFactory
import com.focusclinic.domain.model.InventoryItem
import com.focusclinic.domain.model.ItemType
import com.focusclinic.domain.model.ModifierType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SqlDelightInventoryRepositoryTest {

    private val database = TestDatabaseFactory.create()
    private val repository = SqlDelightInventoryRepository(database)

    private fun equipmentItem(id: String = "item-1") = InventoryItem(
        itemId = id,
        name = "Ergonomic Chair",
        type = ItemType.EQUIPMENT,
        modifier = ModifierType.XpBonus(0.1),
        purchasedAt = 1000L,
    )

    private fun decorationItem(id: String = "decor-1") = InventoryItem(
        itemId = id,
        name = "Wall Paint",
        type = ItemType.DECORATION,
        modifier = ModifierType.None,
        purchasedAt = 1000L,
    )

    @Test
    fun getAll_WhenEmpty_ShouldReturnEmptyList() = runTest {
        assertEquals(emptyList(), repository.getAll())
    }

    @Test
    fun add_AndGetAll_ShouldRoundTrip() = runTest {
        repository.add(equipmentItem())

        val items = repository.getAll()
        assertEquals(1, items.size)
        assertEquals("Ergonomic Chair", items[0].name)
        assertEquals(ItemType.EQUIPMENT, items[0].type)
    }

    @Test
    fun add_ShouldPreserveModifierType() = runTest {
        repository.add(equipmentItem())

        val item = repository.getAll().first()
        assertTrue(item.modifier is ModifierType.XpBonus)
        assertEquals(0.1, (item.modifier as ModifierType.XpBonus).bonusValue)
    }

    @Test
    fun add_DecorationWithNoneModifier_ShouldPreserve() = runTest {
        repository.add(decorationItem())

        val item = repository.getAll().first()
        assertEquals(ModifierType.None, item.modifier)
    }

    @Test
    fun exists_WhenPresent_ShouldReturnTrue() = runTest {
        repository.add(equipmentItem("item-1"))
        assertTrue(repository.exists("item-1"))
    }

    @Test
    fun exists_WhenAbsent_ShouldReturnFalse() = runTest {
        assertFalse(repository.exists("nonexistent"))
    }

    @Test
    fun observeInventory_ShouldEmitCurrentItems() = runTest {
        repository.add(equipmentItem("e1"))
        repository.add(decorationItem("d1"))

        val items = repository.observeInventory().first()
        assertEquals(2, items.size)
    }

    @Test
    fun add_CoinBonusModifier_ShouldRoundTrip() = runTest {
        val item = InventoryItem(
            itemId = "coin-item",
            name = "LED Lamp",
            type = ItemType.EQUIPMENT,
            modifier = ModifierType.CoinBonus(0.05),
            purchasedAt = 1000L,
        )
        repository.add(item)

        val result = repository.getAll().first()
        assertTrue(result.modifier is ModifierType.CoinBonus)
        assertEquals(0.05, (result.modifier as ModifierType.CoinBonus).bonusValue)
    }
}
