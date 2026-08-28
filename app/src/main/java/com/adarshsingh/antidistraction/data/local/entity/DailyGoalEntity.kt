package com.adarshsingh.antidistraction.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_goals")
data class DailyGoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val targetDurationMs: Long,
    val completedDurationMs: Long = 0L,
    val category: String = "GENERAL",
    val priority: String = "MEDIUM",
    val isCompleted: Boolean = false,
    val createdDateStr: String
)
