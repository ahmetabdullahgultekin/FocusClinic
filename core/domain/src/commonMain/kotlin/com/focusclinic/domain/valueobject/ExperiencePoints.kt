package com.focusclinic.domain.valueobject

@kotlin.jvm.JvmInline
value class ExperiencePoints(val value: Long) {

    init {
        require(value >= 0) { "XP cannot be negative: $value" }
    }

    operator fun plus(other: ExperiencePoints): ExperiencePoints =
        ExperiencePoints(value + other.value)

    operator fun compareTo(other: ExperiencePoints): Int =
        value.compareTo(other.value)

    companion object {
        val ZERO = ExperiencePoints(0)
    }
}
