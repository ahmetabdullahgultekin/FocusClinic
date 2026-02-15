package com.focusclinic.app.di

import com.focusclinic.data.database.FocusClinicDatabase
import com.focusclinic.data.database.DriverFactory
import com.focusclinic.data.database.createDatabase
import com.focusclinic.data.repository.SqlDelightCustomRewardRepository
import com.focusclinic.data.repository.SqlDelightFocusSessionRepository
import com.focusclinic.data.repository.SqlDelightInventoryRepository
import com.focusclinic.data.repository.SqlDelightSettingsRepository
import com.focusclinic.data.repository.SqlDelightTransactionRepository
import com.focusclinic.data.repository.SqlDelightUserProfileRepository
import com.focusclinic.data.repository.SqlDelightWillpowerGoalRepository
import com.focusclinic.domain.repository.CustomRewardRepository
import com.focusclinic.domain.repository.FocusSessionRepository
import com.focusclinic.domain.repository.InventoryRepository
import com.focusclinic.domain.repository.SettingsRepository
import com.focusclinic.domain.repository.TransactionRepository
import com.focusclinic.domain.repository.UserProfileRepository
import com.focusclinic.domain.repository.WillpowerGoalRepository
import kotlin.time.Clock
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, kotlin.time.ExperimentalTime::class)
val dataModule = module {

    single<FocusClinicDatabase> {
        createDatabase(get()).also { db ->
            db.userProfileQueries.insertProfile(
                created_at = Clock.System.now().toEpochMilliseconds(),
            )
        }
    }

    single<() -> String>(named("uuid")) { { Uuid.random().toString() } }
    single<() -> Long>(named("clock")) { { Clock.System.now().toEpochMilliseconds() } }

    single<FocusSessionRepository> { SqlDelightFocusSessionRepository(get()) }
    single<UserProfileRepository> {
        SqlDelightUserProfileRepository(
            database = get(),
            clock = get(named("clock")),
        )
    }
    single<InventoryRepository> { SqlDelightInventoryRepository(get()) }
    single<CustomRewardRepository> { SqlDelightCustomRewardRepository(get()) }
    single<TransactionRepository> { SqlDelightTransactionRepository(get()) }
    single<WillpowerGoalRepository> { SqlDelightWillpowerGoalRepository(get()) }
    single<SettingsRepository> { SqlDelightSettingsRepository(get()) }
}
