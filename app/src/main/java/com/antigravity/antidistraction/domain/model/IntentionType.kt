package com.antigravity.antidistraction.domain.model

enum class IntentionType(val displayName: String) {
    IMPORTANT("Important task"),
    QUICK_CHECK("Quick check"),
    HABIT("Automatic habit"),
    BORED("Boredom"),
    COMMUNICATION("Urgent message / call"),
    OTHER("Other reason")
}
