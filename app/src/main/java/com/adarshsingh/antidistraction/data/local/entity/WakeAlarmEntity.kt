package com.adarshsingh.antidistraction.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wake_alarms")
data class WakeAlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String = "Wake Up",
    val timeHour: Int = 7,
    val timeMinute: Int = 0,
    val isEnabled: Boolean = true,
    val repeatDays: String = "DAILY",
    val plannedBedtimeHour: Int = 23,
    val plannedBedtimeMinute: Int = 0,
    val minimumSleepDurationHours: Int = 7
)
