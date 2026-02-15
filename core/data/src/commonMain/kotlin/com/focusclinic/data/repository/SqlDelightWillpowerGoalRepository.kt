package com.focusclinic.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.focusclinic.data.database.FocusClinicDatabase
import com.focusclinic.data.mapper.toDomain
import com.focusclinic.domain.model.GoalCompletion
import com.focusclinic.domain.model.WillpowerGoal
import com.focusclinic.domain.repository.WillpowerGoalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SqlDelightWillpowerGoalRepository(
    private val database: FocusClinicDatabase,
) : WillpowerGoalRepository {

    private val goalQueries get() = database.willpowerGoalsQueries
    private val completionQueries get() = database.goalCompletionsQueries

    override fun observeActiveGoals(): Flow<List<WillpowerGoal>> =
        goalQueries.getActiveGoals()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list -> list.map { it.toDomain() } }

    override fun observeCompletions(goalId: String): Flow<List<GoalCompletion>> =
        completionQueries.getByGoalId(goalId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list -> list.map { it.toDomain() } }

    override fun observeAllCompletions(): Flow<List<GoalCompletion>> =
        completionQueries.getAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list -> list.map { it.toDomain() } }

    override suspend fun getGoalById(id: String): WillpowerGoal? =
        goalQueries.getById(id).executeAsOneOrNull()?.toDomain()

    override suspend fun saveGoal(goal: WillpowerGoal) {
        goalQueries.upsert(
            id = goal.id,
            title = goal.title,
            description = goal.description,
            coin_reward = goal.coinReward.amount,
            xp_reward = goal.xpReward.value,
            is_active = if (goal.isActive) 1L else 0L,
            recurrence_type = goal.recurrenceType.dbValue,
            category = goal.category,
            created_at = goal.createdAt,
            updated_at = goal.updatedAt,
        )
    }

    override suspend fun deactivateGoal(id: String) {
        goalQueries.deactivate(id)
    }

    override suspend fun recordCompletion(completion: GoalCompletion) {
        completionQueries.insert(
            id = completion.id,
            goal_id = completion.goalId,
            completed_at = completion.completedAt,
            earned_coins = completion.earnedCoins.amount,
            earned_xp = completion.earnedXp.value,
            note = completion.note,
        )
    }

    override suspend fun getCompletionsInRange(
        goalId: String,
        startMillis: Long,
        endMillis: Long,
    ): List<GoalCompletion> =
        completionQueries.getByGoalIdInRange(goalId, startMillis, endMillis)
            .executeAsList()
            .map { it.toDomain() }

    override suspend fun getAllCompletionDates(): List<Long> =
        completionQueries.getAllCompletedAtDates()
            .executeAsList()
}
