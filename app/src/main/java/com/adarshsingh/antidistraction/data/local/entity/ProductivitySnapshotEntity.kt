package com.adarshsingh.antidistraction.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productivity_snapshots")
data class ProductivitySnapshotEntity(
    @PrimaryKey
    val dateStr: String,
    val score: Int,
    val focusTimeMinutes: Int,
    val goalsCompletedCount: Int,
    val totalGoalsCount: Int,
    val distractionResistanceRate: Int
)
