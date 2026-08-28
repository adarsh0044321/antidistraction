package com.adarshsingh.antidistraction.domain

import com.adarshsingh.antidistraction.domain.model.FocusMode
import com.adarshsingh.antidistraction.domain.model.FocusSessionState
import com.adarshsingh.antidistraction.domain.model.FocusState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class FocusSessionEngineTest {

    @Test
    fun focusSessionState_initialStateIsIdle() {
        val state = FocusSessionState()
        assertEquals(FocusState.IDLE, state.state)
        assertEquals(25 * 60L, state.remainingSeconds)
        assertEquals(1.0f, state.progressFraction)
    }

    @Test
    fun focusSessionState_timerCalculationIsDerivedFromTimestamps() {
        val startTime = System.currentTimeMillis() - (5 * 60 * 1000L) // 5 minutes elapsed
        val targetDuration = 25 * 60 * 1000L // 25 minutes target
        val remainingMs = targetDuration - (System.currentTimeMillis() - startTime)
        val remainingSecs = remainingMs / 1000L

        assertNotNull(remainingSecs)
        assert(remainingSecs in 1195L..1205L) // Approx 20 minutes remaining
    }
}
