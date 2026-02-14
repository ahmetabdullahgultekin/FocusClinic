package com.focusclinic.app.di

import com.focusclinic.app.presentation.screens.clinic.ClinicViewModel
import com.focusclinic.app.presentation.screens.focus.FocusViewModel
import com.focusclinic.app.presentation.screens.goals.GoalsViewModel
import com.focusclinic.app.presentation.screens.shop.ShopViewModel
import com.focusclinic.app.presentation.screens.stats.StatsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named
import org.koin.dsl.module

val presentationModule = module {

    factory<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.Main) }

    factory {
        FocusViewModel(
            startFocusSession = get(),
            completeFocusSession = get(),
            interruptFocusSession = get(),
            getUserStats = get(),
            timerNotification = get(),
            scope = get(),
        )
    }

    factory {
        ClinicViewModel(
            getUserStats = get(),
            inventoryRepository = get(),
            scope = get(),
        )
    }

    factory {
        ShopViewModel(
            purchaseShopItem = get(),
            purchaseCustomReward = get(),
            saveCustomReward = get(),
            deactivateCustomReward = get(),
            inventoryRepository = get(),
            customRewardRepository = get(),
            transactionRepository = get(),
            scope = get(),
        )
    }

    factory {
        GoalsViewModel(
            createWillpowerGoal = get(),
            completeWillpowerGoal = get(),
            updateWillpowerGoal = get(),
            deactivateWillpowerGoal = get(),
            goalRepository = get(),
            scope = get(),
            clock = get(named("clock")),
        )
    }

    factory {
        StatsViewModel(
            getUserStats = get(),
            sessionRepository = get(),
            scope = get(),
        )
    }
}
