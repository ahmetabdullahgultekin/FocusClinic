package com.focusclinic.domain.rule

import com.focusclinic.domain.model.InventoryItem
import com.focusclinic.domain.model.ItemType
import com.focusclinic.domain.model.ModifierType
import com.focusclinic.domain.valueobject.Multiplier
import kotlin.test.Test
import kotlin.test.assertEquals

class MultiplierCalculatorTest {

    @Test
    fun computeXpMultiplier_WhenEmptyInventory_ShouldReturnBase() {
        val result = MultiplierCalculator.computeXpMultiplier(emptyList())
        assertEquals(1.0, result.value)
    }

    @Test
    fun computeXpMultiplier_WhenSingleXpBonus_ShouldAddBonus() {
        val inventory = listOf(item("a", ModifierType.XpBonus(0.10)))
        val result = MultiplierCalculator.computeXpMultiplier(inventory)
        assertEquals(1.1, result.value)
    }

    @Test
    fun computeXpMultiplier_WhenMultipleXpBonuses_ShouldStackAdditively() {
        val inventory = listOf(
            item("a", ModifierType.XpBonus(0.10)),
            item("b", ModifierType.XpBonus(0.15)),
        )
        val result = MultiplierCalculator.computeXpMultiplier(inventory)
        assertEquals(1.25, result.value)
    }

    @Test
    fun computeXpMultiplier_WhenCoinBonusOnly_ShouldReturnBase() {
        val inventory = listOf(item("a", ModifierType.CoinBonus(0.10)))
        val result = MultiplierCalculator.computeXpMultiplier(inventory)
        assertEquals(1.0, result.value)
    }

    @Test
    fun computeXpMultiplier_WhenExceedsCap_ShouldCapAt3() {
        val inventory = listOf(
            item("a", ModifierType.XpBonus(1.0)),
            item("b", ModifierType.XpBonus(1.0)),
            item("c", ModifierType.XpBonus(1.0)),
        )
        val result = MultiplierCalculator.computeXpMultiplier(inventory)
        assertEquals(3.0, result.value)
    }

    @Test
    fun computeCoinMultiplier_WhenEmptyInventory_ShouldReturnBase() {
        val result = MultiplierCalculator.computeCoinMultiplier(emptyList())
        assertEquals(1.0, result.value)
    }

    @Test
    fun computeCoinMultiplier_WhenSingleCoinBonus_ShouldAddBonus() {
        val inventory = listOf(item("a", ModifierType.CoinBonus(0.05)))
        val result = MultiplierCalculator.computeCoinMultiplier(inventory)
        assertEquals(1.05, result.value)
    }

    @Test
    fun computeCoinMultiplier_WhenXpBonusOnly_ShouldReturnBase() {
        val inventory = listOf(item("a", ModifierType.XpBonus(0.10)))
        val result = MultiplierCalculator.computeCoinMultiplier(inventory)
        assertEquals(1.0, result.value)
    }

    @Test
    fun computeCoinMultiplier_WhenDecorationNone_ShouldReturnBase() {
        val inventory = listOf(item("a", ModifierType.None))
        val result = MultiplierCalculator.computeCoinMultiplier(inventory)
        assertEquals(1.0, result.value)
    }

    private fun item(id: String, modifier: ModifierType) = InventoryItem(
        itemId = id,
        name = "Test Item $id",
        type = ItemType.EQUIPMENT,
        modifier = modifier,
        purchasedAt = 0L,
    )
}
