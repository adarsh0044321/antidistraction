package com.antigravity.antidistraction.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.antigravity.antidistraction.data.local.entity.FocusSessionEntity
import com.antigravity.antidistraction.domain.model.FocusState
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: FocusSessionEntity): Long

    @Update
    suspend fun updateSession(session: FocusSessionEntity)

    @Query("SELECT * FROM focus_sessions WHERE id = :id")
    suspend fun getSessionById(id: Long): FocusSessionEntity?

    @Query("SELECT * FROM focus_sessions WHERE state = :state ORDER BY startTimeMs DESC LIMIT 1")
    suspend fun getLatestSessionWithState(state: FocusState): FocusSessionEntity?

    @Query("SELECT * FROM focus_sessions ORDER BY startTimeMs DESC")
    fun getAllSessionsFlow(): Flow<List<FocusSessionEntity>>

    @Query("SELECT * FROM focus_sessions WHERE startTimeMs >= :startTimeMs ORDER BY startTimeMs DESC")
    fun getSessionsSinceFlow(startTimeMs: Long): Flow<List<FocusSessionEntity>>
}
