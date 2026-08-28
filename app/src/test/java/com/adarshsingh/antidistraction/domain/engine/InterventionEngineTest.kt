package com.adarshsingh.antidistraction.domain.engine

import com.adarshsingh.antidistraction.domain.model.IntentionType
import com.adarshsingh.antidistraction.domain.model.InterventionLevel
import org.junit.Assert.assertEquals
import org.junit.Test

class InterventionEngineTest {

    @Test
    fun deEscalationEngine_productiveClassificationDeEscalatesToLevel0() {
        val engine = DeEscalationEngine()
        val result = engine.deEscalateLevel(
            currentLevel = InterventionLevel.LEVEL_4,
            recentIntentions = emptyList(),
            userClassification = "PRODUCTIVE"
        )
        assertEquals(InterventionLevel.LEVEL_0, result)
    }

    @Test
    fun deEscalationEngine_importantIntentionsDeEscalateByOneLevel() {
        val engine = DeEscalationEngine()
        val result = engine.deEscalateLevel(
            currentLevel = InterventionLevel.LEVEL_4,
            recentIntentions = listOf(IntentionType.IMPORTANT, IntentionType.IMPORTANT),
            userClassification = "NEUTRAL"
        )
        assertEquals(InterventionLevel.LEVEL_3, result)
    }
}
