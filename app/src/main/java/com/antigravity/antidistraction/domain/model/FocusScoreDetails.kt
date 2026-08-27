package com.antigravity.antidistraction.domain.model

data class FocusScoreDetails(
    val totalScore: Int, // 0 to 100
    val completionComponent: Int, // 0 to 40
    val durationComponent: Int, // 0 to 30
    val resistanceComponent: Int, // 0 to 30
    val scoreGrade: String // EXCELLENT, STRONG, MODERATE, BUILDING
)
