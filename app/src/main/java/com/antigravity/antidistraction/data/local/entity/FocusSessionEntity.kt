package com.antigravity.antidistraction.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.antigravity.antidistraction.domain.model.FocusMode
import com.antigravity.antidistraction.domain.model.FocusState

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val profileId: Long,
    val focusMode: FocusMode,
    val startTimeMs: Long,
    val targetDurationMs: Long,
    val actualEndTimeMs: Long? = null,
    val state: FocusState,
    val totalInterventions: Int = 0,
    val totalBypasses: Int = 0,
    val notes: String? = null
)
