package com.focusclinic.domain.valueobject

@JvmInline
value class Coin(val amount: Long) {

    init {
        require(amount >= 0) { "Coin amount cannot be negative: $amount" }
    }

    operator fun plus(other: Coin): Coin = Coin(amount + other.amount)

    operator fun minus(other: Coin): Coin {
        require(amount >= other.amount) { "Insufficient coins: have $amount, need ${other.amount}" }
        return Coin(amount - other.amount)
    }

    operator fun compareTo(other: Coin): Int = amount.compareTo(other.amount)

    companion object {
        val ZERO = Coin(0)
    }
}
