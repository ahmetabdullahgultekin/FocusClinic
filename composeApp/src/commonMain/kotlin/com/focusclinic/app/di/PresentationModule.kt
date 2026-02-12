package com.focusclinic.app.di

import com.focusclinic.app.presentation.screens.focus.FocusViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val presentationModule = module {

    factory<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.Main) }

    factory {
        FocusViewModel(
            startFocusSession = get(),
            completeFocusSession = get(),
            interruptFocusSession = get(),
            getUserStats = get(),
            scope = get(),
        )
    }
}
