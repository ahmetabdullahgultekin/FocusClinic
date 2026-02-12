package com.focusclinic.domain.repository

import com.focusclinic.domain.model.FocusSession
import kotlinx.coroutines.flow.Flow

interface FocusSessionRepository {
    fun observeActiveSession(): Flow<FocusSession?>
    fun observeSessionHistory(): Flow<List<FocusSession>>
    suspend fun save(session: FocusSession)
    suspend fun getById(id: String): FocusSession?
}
