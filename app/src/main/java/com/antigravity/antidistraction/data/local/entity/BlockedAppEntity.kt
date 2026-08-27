package com.antigravity.antidistraction.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.antigravity.antidistraction.domain.model.InterventionLevel

@Entity(tableName = "blocked_apps")
data class BlockedAppEntity(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val category: String = "Uncategorized",
    val customRestrictionLevel: InterventionLevel? = null,
    val userClassification: String = "NEUTRAL", // PRODUCTIVE, NEUTRAL, DISTRACTING
    val isEmergencyApp: Boolean = false,
    val distractionScore: Int = 0, // 0 to 100
    val lastAccessedTimeMs: Long = 0
)
