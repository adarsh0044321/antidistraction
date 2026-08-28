package com.adarshsingh.antidistraction.domain.repository

import com.adarshsingh.antidistraction.data.local.entity.FocusSessionEntity
import com.adarshsingh.antidistraction.domain.model.FocusState
import kotlinx.coroutines.flow.Flow

interface FocusSessionRepository {
    suspend fun createSession(profileId: Long, durationMs: Long): Long
    suspend fun updateSessionState(sessionId: Long, state: FocusState, actualEndTimeMs: Long? = null)
    suspend fun getSessionById(sessionId: Long): FocusSessionEntity?
    suspend fun getActiveSession(): FocusSessionEntity?
    fun getAllSessions(): Flow<List<FocusSessionEntity>>
    suspend fun incrementInterventionCount(sessionId: Long)
}
