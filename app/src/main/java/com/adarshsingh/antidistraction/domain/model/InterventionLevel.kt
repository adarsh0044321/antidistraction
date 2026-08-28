package com.adarshsingh.antidistraction.domain.model

enum class InterventionLevel(val severity: Int, val description: String) {
    LEVEL_0(0, "No intervention"),
    LEVEL_1(1, "Subtle warning"),
    LEVEL_2(2, "Confirmation required"),
    LEVEL_3(3, "Intention check"),
    LEVEL_4(4, "Short deliberate delay"),
    LEVEL_5(5, "Strong restriction"),
    LEVEL_6(6, "Hard restriction")
}
