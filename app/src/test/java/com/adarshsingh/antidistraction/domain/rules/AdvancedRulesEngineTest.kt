package com.adarshsingh.antidistraction.domain.rules

import com.adarshsingh.antidistraction.domain.model.CompositeRule
import com.adarshsingh.antidistraction.domain.model.FocusMode
import com.adarshsingh.antidistraction.domain.model.RuleActionType
import com.adarshsingh.antidistraction.domain.model.RuleConditionType
import org.junit.Assert.assertEquals
import org.junit.Test

class AdvancedRulesEngineTest {

    @Test
    fun evaluateCompositeRules_matchesFocusModeCondition() {
        val engine = AdvancedRulesEngine()
        val rule = CompositeRule(
            id = "1",
            name = "Deep Focus Hard Block",
            conditionType = RuleConditionType.FOCUS_MODE_EQUALS,
            conditionValue = "DEEP_FOCUS",
            actionType = RuleActionType.ENFORCE_HARD_RESTRICTION
        )
        val action = engine.evaluateCompositeRules(
            rules = listOf(rule),
            currentMode = FocusMode.DEEP_FOCUS,
            attemptCount = 1,
            category = "Social Media",
            isBreakActive = false
        )
        assertEquals(RuleActionType.ENFORCE_HARD_RESTRICTION, action)
    }
}
