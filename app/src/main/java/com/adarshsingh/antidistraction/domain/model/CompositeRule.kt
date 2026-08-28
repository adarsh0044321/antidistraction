package com.adarshsingh.antidistraction.domain.model

enum class RuleConditionType {
    FOCUS_MODE_EQUALS,
    ATTEMPTS_GREATER_THAN,
    APP_CATEGORY_EQUALS,
    BREAK_ACTIVE
}

enum class RuleActionType {
    ENFORCE_HARD_RESTRICTION,
    REQUIRE_INTENTION,
    RELAX_RESTRICTION,
    APPLY_DELAY
}

data class CompositeRule(
    val id: String,
    val name: String,
    val conditionType: RuleConditionType,
    val conditionValue: String,
    val actionType: RuleActionType,
    val isEnabled: Boolean = true
)
