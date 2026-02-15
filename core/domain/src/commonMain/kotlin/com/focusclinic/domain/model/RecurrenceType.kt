package com.focusclinic.domain.model

sealed interface RecurrenceType {
    data object None : RecurrenceType
    data object Daily : RecurrenceType
    data object Weekly : RecurrenceType

    val dbValue: String
        get() = when (this) {
            None -> DB_NONE
            Daily -> DB_DAILY
            Weekly -> DB_WEEKLY
        }

    companion object {
        private const val DB_NONE = "NONE"
        private const val DB_DAILY = "DAILY"
        private const val DB_WEEKLY = "WEEKLY"

        fun fromDbValue(value: String): RecurrenceType = when (value) {
            DB_DAILY -> Daily
            DB_WEEKLY -> Weekly
            else -> None
        }
    }
}
