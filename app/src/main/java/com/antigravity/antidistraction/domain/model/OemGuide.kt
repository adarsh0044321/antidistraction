package com.antigravity.antidistraction.domain.model

data class OemGuide(
    val manufacturerName: String,
    val requiresAutostart: Boolean,
    val requiresBatteryExemption: Boolean,
    val setupInstructions: List<String>
)
