package com.focusclinic.domain.model

sealed interface DomainError {
    val message: String

    sealed interface FocusError : DomainError {
        data object SessionAlreadyActive : FocusError {
            override val message = "A focus session is already in progress"
        }
        data object NoActiveSession : FocusError {
            override val message = "No active focus session to complete"
        }
    }

    sealed interface PurchaseError : DomainError {
        data class InsufficientCoins(val required: Long, val available: Long) : PurchaseError {
            override val message = "Insufficient coins: need $required, have $available"
        }
        data class ItemAlreadyOwned(val itemId: String) : PurchaseError {
            override val message = "Item already owned: $itemId"
        }
        data class RewardNotFound(val rewardId: String) : PurchaseError {
            override val message = "Reward not found: $rewardId"
        }
    }
}
