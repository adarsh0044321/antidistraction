package com.adarshsingh.antidistraction.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.adarshsingh.antidistraction.data.local.entity.DistractionAttemptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DistractionAttemptDao {
    @Insert
    suspend fun insertAttempt(attempt: DistractionAttemptEntity): Long

    @Query("SELECT * FROM distraction_attempts WHERE packageName = :packageName ORDER BY timestampMs DESC")
    fun getAttemptsForAppFlow(packageName: String): Flow<List<DistractionAttemptEntity>>

    @Query("SELECT COUNT(*) FROM distraction_attempts WHERE packageName = :packageName AND timestampMs >= :sinceMs")
    suspend fun getRecentAttemptCountForApp(packageName: String, sinceMs: Long): Int

    @Query("SELECT * FROM distraction_attempts WHERE timestampMs >= :sinceMs ORDER BY timestampMs DESC")
    fun getAttemptsSinceFlow(sinceMs: Long): Flow<List<DistractionAttemptEntity>>
}
