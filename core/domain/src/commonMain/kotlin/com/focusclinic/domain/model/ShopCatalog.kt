package com.focusclinic.domain.model

import com.focusclinic.domain.valueobject.Coin

object ShopCatalog {

    val items: List<ShopItem> = listOf(
        ShopItem(
            id = "ergonomic_chair",
            name = "Ergonomic Chair",
            type = ItemType.EQUIPMENT,
            modifier = ModifierType.XpBonus(0.10),
            cost = Coin(500),
        ),
        ShopItem(
            id = "led_lamp",
            name = "LED Lamp",
            type = ItemType.EQUIPMENT,
            modifier = ModifierType.CoinBonus(0.05),
            cost = Coin(300),
        ),
        ShopItem(
            id = "sterilizer_pro",
            name = "Sterilizer Pro",
            type = ItemType.EQUIPMENT,
            modifier = ModifierType.XpBonus(0.15),
            cost = Coin(800),
        ),
        ShopItem(
            id = "digital_xray",
            name = "Digital X-Ray",
            type = ItemType.EQUIPMENT,
            modifier = ModifierType.CoinBonus(0.10),
            cost = Coin(600),
        ),
        ShopItem(
            id = "wall_paint_ocean",
            name = "Wall Paint: Ocean",
            type = ItemType.DECORATION,
            modifier = ModifierType.None,
            cost = Coin(200),
        ),
        ShopItem(
            id = "diploma_frame",
            name = "Diploma Frame",
            type = ItemType.DECORATION,
            modifier = ModifierType.None,
            cost = Coin(150),
        ),
        ShopItem(
            id = "potted_plant",
            name = "Potted Plant",
            type = ItemType.DECORATION,
            modifier = ModifierType.None,
            cost = Coin(100),
        ),
        ShopItem(
            id = "aquarium",
            name = "Waiting Room Aquarium",
            type = ItemType.DECORATION,
            modifier = ModifierType.None,
            cost = Coin(400),
        ),
    )
}
