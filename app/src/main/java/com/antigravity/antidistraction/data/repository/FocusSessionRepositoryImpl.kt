package com.antigravity.antidistraction.data.repository

import com.antigravity.antidistraction.data.local.dao.FocusProfileDao
import com.antigravity.antidistraction.data.local.dao.FocusSessionDao
import com.antigravity.antidistraction.data.local.entity.FocusSessionEntity
import com.antigravity.antidistraction.domain.model.FocusMode
import com.antigravity.antidistraction.domain.model.FocusState
import com.antigravity.antidistraction.domain.repository.FocusSessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FocusSessionRepositoryImpl @Inject constructor(
    private val sessionDao: FocusSessionDao,
    private val profileDao: FocusProfileDao
) : FocusSessionRepository {

    override suspend fun createSession(profileId: Long, durationMs: Long): Long {
        val profile = profileDao.getProfileById(profileId)
        val mode = profile?.mode ?: FocusMode.DEEP_FOCUS

        val entity = FocusSessionEntity(
            profileId = profileId,
            focusMode = mode,
            startTimeMs = System.currentTimeMillis(),
            targetDurationMs = durationMs,
            state = FocusState.FOCUS_ACTIVE
        )
        return sessionDao.insertSession(entity)
    }

    override suspend fun updateSessionState(sessionId: Long, state: FocusState, actualEndTimeMs: Long?) {
        val session = sessionDao.getSessionById(sessionId) ?: return
        val updated = session.copy(
            state = state,
            actualEndTimeMs = actualEndTimeMs ?: session.actualEndTimeMs
        )
        sessionDao.updateSession(updated)
    }

    override suspend fun getSessionById(sessionId: Long): FocusSessionEntity? {
        return sessionDao.getSessionById(sessionId)
    }

    override suspend fun getActiveSession(): FocusSessionEntity? {
        val activeState = sessionDao.getLatestSessionWithState(FocusState.FOCUS_ACTIVE)
            ?: sessionDao.getLatestSessionWithState(FocusState.PAUSED)
            ?: sessionDao.getLatestSessionWithState(FocusState.RESUMED)
        return activeState
    }

    override fun getAllSessions(): Flow<List<FocusSessionEntity>> {
        return sessionDao.getAllSessionsFlow()
    }
}
