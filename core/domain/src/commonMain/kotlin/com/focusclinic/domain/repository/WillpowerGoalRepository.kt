package com.focusclinic.domain.repository

import com.focusclinic.domain.model.GoalCompletion
import com.focusclinic.domain.model.WillpowerGoal
import kotlinx.coroutines.flow.Flow

interface WillpowerGoalRepository {
    fun observeActiveGoals(): Flow<List<WillpowerGoal>>
    fun observeCompletions(goalId: String): Flow<List<GoalCompletion>>
    fun observeAllCompletions(): Flow<List<GoalCompletion>>
    suspend fun getGoalById(id: String): WillpowerGoal?
    suspend fun saveGoal(goal: WillpowerGoal)
    suspend fun deactivateGoal(id: String)
    suspend fun recordCompletion(completion: GoalCompletion)
    suspend fun getCompletionsInRange(goalId: String, startMillis: Long, endMillis: Long): List<GoalCompletion>
    suspend fun getAllCompletionDates(): List<Long>
}
