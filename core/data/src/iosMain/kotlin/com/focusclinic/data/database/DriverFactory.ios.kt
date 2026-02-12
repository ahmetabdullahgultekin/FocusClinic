package com.focusclinic.data.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class DriverFactory {
    actual fun create(): SqlDriver {
        return NativeSqliteDriver(FocusClinicDatabase.Schema, "focusclinic.db")
    }
}
