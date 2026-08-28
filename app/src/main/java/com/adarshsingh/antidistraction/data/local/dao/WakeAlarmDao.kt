package com.adarshsingh.antidistraction.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.adarshsingh.antidistraction.data.local.entity.WakeAlarmEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WakeAlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: WakeAlarmEntity): Long

    @Update
    suspend fun updateAlarm(alarm: WakeAlarmEntity)

    @Query("SELECT * FROM wake_alarms ORDER BY id DESC")
    fun getAllAlarmsFlow(): Flow<List<WakeAlarmEntity>>
}
