package com.focusclinic.data

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.focusclinic.data.database.FocusClinicDatabase

object TestDatabaseFactory {

    fun create(): FocusClinicDatabase {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        FocusClinicDatabase.Schema.create(driver)
        return FocusClinicDatabase(driver)
    }
}
