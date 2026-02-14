package com.focusclinic.app.di

import com.focusclinic.app.platform.HapticFeedback
import com.focusclinic.app.platform.HapticFeedbackManager
import com.focusclinic.app.platform.TimerNotification
import com.focusclinic.app.platform.TimerNotificationManager
import com.focusclinic.data.database.DriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { DriverFactory() }
    single<TimerNotification> { TimerNotificationManager() }
    single<HapticFeedback> { HapticFeedbackManager() }
}
