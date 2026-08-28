package com.adarshsingh.antidistraction.domain.engine

import com.adarshsingh.antidistraction.domain.model.BreakMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class BreakEngineTest {

    @Test
    fun breakEngine_initialStateIsNotActive() {
        val engine = BreakEngine()
        assertFalse(engine.breakState.value.isBreakActive)
        assertEquals(BreakMode.POMODORO_25_5, engine.breakState.value.breakMode)
    }

    @Test
    fun setBreakMode_updatesBreakDuration() {
        val engine = BreakEngine()
        engine.setBreakMode(BreakMode.LONG_CYCLE_50_10)
        assertEquals(10, engine.breakState.value.breakDurationMinutes)
    }
}
