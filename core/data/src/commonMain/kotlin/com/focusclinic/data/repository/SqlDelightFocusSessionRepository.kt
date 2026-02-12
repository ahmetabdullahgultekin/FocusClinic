package com.focusclinic.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.focusclinic.data.database.FocusClinicDatabase
import com.focusclinic.data.mapper.toDomain
import com.focusclinic.data.mapper.toDbString
import com.focusclinic.domain.model.FocusSession
import com.focusclinic.domain.repository.FocusSessionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SqlDelightFocusSessionRepository(
    private val database: FocusClinicDatabase,
) : FocusSessionRepository {

    private val queries get() = database.focusSessionsQueries

    override fun observeActiveSession(): Flow<FocusSession?> =
        queries.getActiveSession()
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { it?.toDomain() }

    override fun observeSessionHistory(): Flow<List<FocusSession>> =
        queries.getAllByStartTimeDesc()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list -> list.map { it.toDomain() } }

    override suspend fun save(session: FocusSession) {
        queries.upsert(
            id = session.id,
            start_time = session.startTime,
            end_time = session.endTime,
            planned_duration_minutes = session.plannedDuration.minutes.toLong(),
            actual_focus_minutes = session.actualFocusMinutes.toLong(),
            status = session.status.toDbString(),
            earned_xp = session.earnedXp.value,
            earned_coins = session.earnedCoins.amount,
        )
    }

    override suspend fun getById(id: String): FocusSession? =
        queries.getById(id).executeAsOneOrNull()?.toDomain()
}
