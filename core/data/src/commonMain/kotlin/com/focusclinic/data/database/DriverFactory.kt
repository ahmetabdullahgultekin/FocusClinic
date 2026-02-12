package com.focusclinic.data.database

import app.cash.sqldelight.db.SqlDriver

expect class DriverFactory {
    fun create(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): FocusClinicDatabase {
    return FocusClinicDatabase(driverFactory.create())
}
