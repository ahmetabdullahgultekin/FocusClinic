package com.focusclinic.domain.model

import com.focusclinic.domain.valueobject.Coin

object ShopCatalog {

    val items: List<ShopItem> = listOf(
        ShopItem(
            id = "focus_stone",
            name = "Focus Stone",
            type = ItemType.EQUIPMENT,
            modifier = ModifierType.XpBonus(0.10),
            cost = Coin(500),
        ),
        ShopItem(
            id = "perseverance_shield",
            name = "Perseverance Shield",
            type = ItemType.EQUIPMENT,
            modifier = ModifierType.CoinBonus(0.05),
            cost = Coin(300),
        ),
        ShopItem(
            id = "willpower_fire",
            name = "Willpower Fire",
            type = ItemType.EQUIPMENT,
            modifier = ModifierType.XpBonus(0.15),
            cost = Coin(800),
        ),
        ShopItem(
            id = "patience_medal",
            name = "Patience Medal",
            type = ItemType.EQUIPMENT,
            modifier = ModifierType.CoinBonus(0.10),
            cost = Coin(600),
        ),
        ShopItem(
            id = "peace_garden",
            name = "Peace Garden",
            type = ItemType.DECORATION,
            modifier = ModifierType.None,
            cost = Coin(200),
        ),
        ShopItem(
            id = "motivation_wall",
            name = "Motivation Wall",
            type = ItemType.DECORATION,
            modifier = ModifierType.None,
            cost = Coin(150),
        ),
        ShopItem(
            id = "inspiration_plant",
            name = "Inspiration Plant",
            type = ItemType.DECORATION,
            modifier = ModifierType.None,
            cost = Coin(100),
        ),
        ShopItem(
            id = "victory_aquarium",
            name = "Victory Aquarium",
            type = ItemType.DECORATION,
            modifier = ModifierType.None,
            cost = Coin(400),
        ),
    )
}
