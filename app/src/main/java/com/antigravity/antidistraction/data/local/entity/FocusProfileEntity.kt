package com.antigravity.antidistraction.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.antigravity.antidistraction.domain.model.FocusMode

@Entity(tableName = "focus_profiles")
data class FocusProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val mode: FocusMode,
    val isDefault: Boolean = false,
    val allowBreak: Boolean = true,
    val breakDurationMinutes: Int = 5,
    val workDurationMinutes: Int = 25,
    val allowBypass: Boolean = true,
    val maxBypassesAllowed: Int = 3
)
