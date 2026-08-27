package com.antigravity.antidistraction.domain.model

import com.antigravity.antidistraction.domain.model.InterventionLevel

data class AppInfo(
    val packageName: String,
    val appName: String,
    val category: String,
    val isBlocked: Boolean = false,
    val isEmergencyApp: Boolean = false,
    val customRestrictionLevel: InterventionLevel? = null,
    val userClassification: String = "NEUTRAL", // PRODUCTIVE, NEUTRAL, DISTRACTING
    val distractionScore: Int = 0
)
