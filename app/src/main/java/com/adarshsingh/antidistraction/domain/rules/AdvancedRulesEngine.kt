package com.adarshsingh.antidistraction.domain.rules

import com.adarshsingh.antidistraction.domain.model.CompositeRule
import com.adarshsingh.antidistraction.domain.model.FocusMode
import com.adarshsingh.antidistraction.domain.model.InterventionLevel
import com.adarshsingh.antidistraction.domain.model.RuleActionType
import com.adarshsingh.antidistraction.domain.model.RuleConditionType
import com.adarshsingh.antidistraction.util.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdvancedRulesEngine @Inject constructor() {

    fun evaluateCompositeRules(
        rules: List<CompositeRule>,
        currentMode: FocusMode,
        attemptCount: Int,
        category: String,
        isBreakActive: Boolean
    ): RuleActionType? {
        val activeRules = rules.filter { it.isEnabled }

        for (rule in activeRules) {
            val matches = when (rule.conditionType) {
                RuleConditionType.FOCUS_MODE_EQUALS -> rule.conditionValue == currentMode.name
                RuleConditionType.ATTEMPTS_GREATER_THAN -> attemptCount > (rule.conditionValue.toIntOrNull() ?: 0)
                RuleConditionType.APP_CATEGORY_EQUALS -> rule.conditionValue.equals(category, ignoreCase = true)
                RuleConditionType.BREAK_ACTIVE -> isBreakActive
            }

            if (matches) {
                Logger.i("AdvancedRulesEngine", "Rule matched: ${rule.name} -> Action: ${rule.actionType.name}")
                return rule.actionType
            }
        }
        return null
    }
}
