package com.adarshsingh.antidistraction.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.adarshsingh.antidistraction.domain.model.FocusMode

@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val profileId: Long,
    val mode: FocusMode,
    val daysOfWeekMask: Int, // Bitmask: Bit 1=Mon, Bit 2=Tue, ..., Bit 7=Sun
    val startMinuteOfDay: Int, // 0 to 1439
    val endMinuteOfDay: Int, // 0 to 1439
    val isEnabled: Boolean = true
)
