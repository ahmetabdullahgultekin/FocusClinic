package com.focusclinic.domain.repository

import com.focusclinic.domain.model.CustomReward
import kotlinx.coroutines.flow.Flow

interface CustomRewardRepository {
    fun observeActiveRewards(): Flow<List<CustomReward>>
    suspend fun getById(id: String): CustomReward?
    suspend fun save(reward: CustomReward)
    suspend fun deactivate(id: String)
}
