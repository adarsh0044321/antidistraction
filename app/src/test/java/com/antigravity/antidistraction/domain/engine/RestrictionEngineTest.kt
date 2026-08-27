package com.antigravity.antidistraction.domain.engine

import com.antigravity.antidistraction.domain.model.RestrictionDecision
import org.junit.Assert.assertEquals
import org.junit.Test

class RestrictionEngineTest {

    @Test
    fun restrictionDecision_allDecisionsExist() {
        val decisions = RestrictionDecision.values()
        assertEquals(6, decisions.size)
        assertEquals(RestrictionDecision.EMERGENCY_ALLOWED, RestrictionDecision.valueOf("EMERGENCY_ALLOWED"))
    }
}
