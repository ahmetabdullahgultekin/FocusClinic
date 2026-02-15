package com.focusclinic.app.di

import com.focusclinic.domain.usecase.CalculateStreakUseCase
import com.focusclinic.domain.usecase.CompleteFocusSessionUseCase
import com.focusclinic.domain.usecase.CompleteWillpowerGoalUseCase
import com.focusclinic.domain.usecase.CreateWillpowerGoalUseCase
import com.focusclinic.domain.usecase.DeactivateCustomRewardUseCase
import com.focusclinic.domain.usecase.DeactivateWillpowerGoalUseCase
import com.focusclinic.domain.usecase.GetUserStatsUseCase
import com.focusclinic.domain.usecase.InterruptFocusSessionUseCase
import com.focusclinic.domain.usecase.IsGoalCompletableUseCase
import com.focusclinic.domain.usecase.PurchaseCustomRewardUseCase
import com.focusclinic.domain.usecase.PurchaseShopItemUseCase
import com.focusclinic.domain.usecase.SaveCustomRewardUseCase
import com.focusclinic.domain.usecase.StartFocusSessionUseCase
import com.focusclinic.domain.usecase.UpdateWillpowerGoalUseCase
import org.koin.core.qualifier.named
import org.koin.dsl.module

val domainModule = module {

    factory {
        StartFocusSessionUseCase(
            sessionRepository = get(),
            idGenerator = get(named("uuid")),
            clock = get(named("clock")),
        )
    }

    factory {
        CompleteFocusSessionUseCase(
            sessionRepository = get(),
            userProfileRepository = get(),
            inventoryRepository = get(),
            transactionRepository = get(),
            idGenerator = get(named("uuid")),
            clock = get(named("clock")),
        )
    }

    factory {
        InterruptFocusSessionUseCase(
            sessionRepository = get(),
            userProfileRepository = get(),
            inventoryRepository = get(),
            transactionRepository = get(),
            idGenerator = get(named("uuid")),
            clock = get(named("clock")),
        )
    }

    factory {
        PurchaseShopItemUseCase(
            inventoryRepository = get(),
            transactionRepository = get(),
            idGenerator = get(named("uuid")),
            clock = get(named("clock")),
        )
    }

    factory {
        PurchaseCustomRewardUseCase(
            rewardRepository = get(),
            transactionRepository = get(),
            idGenerator = get(named("uuid")),
            clock = get(named("clock")),
        )
    }

    factory {
        GetUserStatsUseCase(
            userProfileRepository = get(),
            transactionRepository = get(),
            inventoryRepository = get(),
        )
    }

    factory {
        SaveCustomRewardUseCase(
            rewardRepository = get(),
            idGenerator = get(named("uuid")),
            clock = get(named("clock")),
        )
    }

    factory {
        DeactivateCustomRewardUseCase(
            rewardRepository = get(),
        )
    }

    factory {
        CreateWillpowerGoalUseCase(
            goalRepository = get(),
            idGenerator = get(named("uuid")),
            clock = get(named("clock")),
        )
    }

    factory {
        CalculateStreakUseCase(
            goalRepository = get(),
            clock = get(named("clock")),
        )
    }

    factory {
        IsGoalCompletableUseCase(
            goalRepository = get(),
            clock = get(named("clock")),
        )
    }

    factory {
        CompleteWillpowerGoalUseCase(
            goalRepository = get(),
            transactionRepository = get(),
            userProfileRepository = get(),
            calculateStreak = get(),
            idGenerator = get(named("uuid")),
            clock = get(named("clock")),
        )
    }

    factory {
        UpdateWillpowerGoalUseCase(
            goalRepository = get(),
            clock = get(named("clock")),
        )
    }

    factory {
        DeactivateWillpowerGoalUseCase(
            goalRepository = get(),
        )
    }
}
