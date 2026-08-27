package com.antigravity.antidistraction.domain

import com.antigravity.antidistraction.domain.model.FocusMode
import com.antigravity.antidistraction.domain.model.FocusState
import com.antigravity.antidistraction.domain.model.IntentionType
import com.antigravity.antidistraction.domain.model.InterventionLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class FocusStateTest {

    @Test
    fun focusState_allStatesExist() {
        val states = FocusState.values()
        assertEquals(11, states.size)
        assertNotNull(FocusState.IDLE)
        assertNotNull(FocusState.FOCUS_ACTIVE)
        assertNotNull(FocusState.INTERVENTION)
    }

    @Test
    fun focusMode_allModesExist() {
        val modes = FocusMode.values()
        assertEquals(5, modes.size)
        assertEquals(FocusMode.DEEP_FOCUS, FocusMode.valueOf("DEEP_FOCUS"))
    }

    @Test
    fun interventionLevel_severitiesAreOrdered() {
        assertEquals(0, InterventionLevel.LEVEL_0.severity)
        assertEquals(1, InterventionLevel.LEVEL_1.severity)
        assertEquals(6, InterventionLevel.LEVEL_6.severity)
    }

    @Test
    fun intentionType_displayNamesNotEmpty() {
        IntentionType.values().forEach { intention ->
            assert(intention.displayName.isNotBlank())
        }
    }
}
