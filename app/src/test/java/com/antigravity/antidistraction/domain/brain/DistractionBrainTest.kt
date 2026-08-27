package com.antigravity.antidistraction.domain.brain

import com.antigravity.antidistraction.data.local.dao.BlockedAppDao
import com.antigravity.antidistraction.data.local.dao.DistractionAttemptDao
import com.antigravity.antidistraction.data.local.entity.BlockedAppEntity
import com.antigravity.antidistraction.data.local.entity.DistractionAttemptEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Test

class FakeAttemptDao : DistractionAttemptDao {
    override suspend fun insertAttempt(attempt: DistractionAttemptEntity): Long = 1L
    override fun getAttemptsForAppFlow(packageName: String): Flow<List<DistractionAttemptEntity>> = flowOf(emptyList())
    override suspend fun getRecentAttemptCountForApp(packageName: String, sinceMs: Long): Int = 0
    override fun getAttemptsSinceFlow(sinceMs: Long): Flow<List<DistractionAttemptEntity>> = flowOf(emptyList())
}

class FakeBlockedAppDao : BlockedAppDao {
    override suspend fun insertOrUpdateApp(app: BlockedAppEntity) {}
    override suspend fun insertAll(apps: List<BlockedAppEntity>) {}
    override suspend fun updateApp(app: BlockedAppEntity) {}
    override suspend fun deleteApp(app: BlockedAppEntity) {}
    override suspend fun getAppByPackageName(packageName: String): BlockedAppEntity? = null
    override fun getAllAppsFlow(): Flow<List<BlockedAppEntity>> = flowOf(emptyList())
    override fun getEmergencyAppsFlow(): Flow<List<BlockedAppEntity>> = flowOf(emptyList())
    override suspend fun updateDistractionScore(packageName: String, score: Int) {}
}

class DistractionBrainTest {

    @Test
    fun calculateDistractionScore_boundedBetween0And100() {
        val brain = DistractionBrain(
            attemptDao = FakeAttemptDao(),
            blockedAppDao = FakeBlockedAppDao()
        )

        val scoreHigh = brain.calculateDistractionScore(10, 50, 40, "DISTRACTING", 22)
        assertEquals(100, scoreHigh)

        val scoreLow = brain.calculateDistractionScore(0, 0, 0, "PRODUCTIVE", 12)
        assertEquals(0, scoreLow)
    }
}
