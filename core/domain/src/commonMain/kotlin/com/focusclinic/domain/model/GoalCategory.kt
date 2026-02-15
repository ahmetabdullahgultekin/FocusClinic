package com.focusclinic.domain.model

object GoalCategory {
    const val NONE = ""
    const val HEALTH = "health"
    const val PRODUCTIVITY = "productivity"
    const val LEARNING = "learning"
    const val FITNESS = "fitness"
    const val HABITS = "habits"
    const val OTHER = "other"

    val PREDEFINED = listOf(HEALTH, PRODUCTIVITY, LEARNING, FITNESS, HABITS, OTHER)
}
