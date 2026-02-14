package com.focusclinic.domain.model

sealed interface TransactionType {
    data object EarnFocus : TransactionType
    data object EarnGoal : TransactionType
    data object SpendShop : TransactionType
    data object SpendReward : TransactionType
}

data class Transaction(
    val id: String,
    val type: TransactionType,
    val amount: Long,
    val referenceId: String,
    val description: String,
    val createdAt: Long,
)
