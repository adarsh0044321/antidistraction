package com.adarshsingh.antidistraction.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.adarshsingh.antidistraction.data.local.entity.DailyGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyGoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: DailyGoalEntity): Long

    @Update
    suspend fun updateGoal(goal: DailyGoalEntity)

    @Query("SELECT * FROM daily_goals WHERE createdDateStr = :dateStr ORDER BY id DESC")
    fun getGoalsForDateFlow(dateStr: String): Flow<List<DailyGoalEntity>>

    @Query("UPDATE daily_goals SET completedDurationMs = completedDurationMs + :addedMs, isCompleted = CASE WHEN (completedDurationMs + :addedMs) >= targetDurationMs THEN 1 ELSE isCompleted END WHERE id = :goalId")
    suspend fun addGoalProgress(goalId: Long, addedMs: Long)
}
