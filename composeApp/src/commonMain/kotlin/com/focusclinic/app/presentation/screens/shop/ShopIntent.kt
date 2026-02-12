package com.focusclinic.app.presentation.screens.shop

import com.focusclinic.domain.model.ShopItem

sealed interface ShopIntent {
    data class PurchaseItem(val item: ShopItem) : ShopIntent
    data class PurchaseReward(val rewardId: String) : ShopIntent
    data class CreateReward(val title: String, val cost: Long) : ShopIntent
    data class DeleteReward(val rewardId: String) : ShopIntent
    data object DismissError : ShopIntent
    data object DismissPurchaseSuccess : ShopIntent
    data class SelectTab(val tab: ShopTab) : ShopIntent
}

enum class ShopTab { VIRTUAL_SHOP, CUSTOM_REWARDS }
