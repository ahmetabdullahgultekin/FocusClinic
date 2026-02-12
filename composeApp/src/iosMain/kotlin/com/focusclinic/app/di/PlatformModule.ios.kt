package com.focusclinic.app.di

import com.focusclinic.data.database.DriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { DriverFactory() }
}
