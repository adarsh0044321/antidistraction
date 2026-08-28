package com.adarshsingh.antidistraction.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.adarshsingh.antidistraction.domain.model.IntentionType
import com.adarshsingh.antidistraction.domain.model.InterventionLevel

@Entity(tableName = "distraction_attempts")
data class DistractionAttemptEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long?,
    val packageName: String,
    val timestampMs: Long,
    val intentionChoice: IntentionType?,
    val interventionLevelApplied: InterventionLevel,
    val bypassGranted: Boolean,
    val userActionTaken: String // RETURNED_TO_FOCUS, BYPASSED, EMERGENCY
)
