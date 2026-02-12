package com.focusclinic.app.presentation.screens.shop

import com.focusclinic.domain.model.CustomReward
import com.focusclinic.domain.model.ShopItem
import com.focusclinic.domain.valueobject.Coin

data class ShopState(
    val selectedTab: ShopTab = ShopTab.VIRTUAL_SHOP,
    val shopItems: List<ShopItem> = emptyList(),
    val ownedItemIds: Set<String> = emptySet(),
    val customRewards: List<CustomReward> = emptyList(),
    val balance: Coin = Coin.ZERO,
    val isProcessing: Boolean = false,
    val errorMessage: String? = null,
    val purchaseSuccessMessage: String? = null,
)
