package com.focusclinic.data.mapper

import com.focusclinic.domain.model.TransactionType

private const val TYPE_EARN_FOCUS = "EARN_FOCUS"
private const val TYPE_SPEND_SHOP = "SPEND_SHOP"
private const val TYPE_SPEND_REWARD = "SPEND_REWARD"

fun TransactionType.toDbString(): String = when (this) {
    TransactionType.EarnFocus -> TYPE_EARN_FOCUS
    TransactionType.SpendShop -> TYPE_SPEND_SHOP
    TransactionType.SpendReward -> TYPE_SPEND_REWARD
}

fun String.toTransactionType(): TransactionType = when (this) {
    TYPE_EARN_FOCUS -> TransactionType.EarnFocus
    TYPE_SPEND_SHOP -> TransactionType.SpendShop
    TYPE_SPEND_REWARD -> TransactionType.SpendReward
    else -> TransactionType.SpendReward
}
