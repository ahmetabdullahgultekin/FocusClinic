package com.focusclinic.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.focusclinic.data.database.FocusClinicDatabase
import com.focusclinic.data.mapper.toDomain
import com.focusclinic.domain.model.CustomReward
import com.focusclinic.domain.repository.CustomRewardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SqlDelightCustomRewardRepository(
    private val database: FocusClinicDatabase,
) : CustomRewardRepository {

    private val queries get() = database.customRewardsQueries

    override fun observeActiveRewards(): Flow<List<CustomReward>> =
        queries.getActiveRewards()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: String): CustomReward? =
        queries.getById(id).executeAsOneOrNull()?.toDomain()

    override suspend fun save(reward: CustomReward) {
        queries.upsert(
            id = reward.id,
            title = reward.title,
            cost = reward.cost.amount,
            is_active = if (reward.isActive) 1L else 0L,
            created_at = reward.createdAt,
        )
    }

    override suspend fun deactivate(id: String) {
        queries.deactivate(id)
    }
}
