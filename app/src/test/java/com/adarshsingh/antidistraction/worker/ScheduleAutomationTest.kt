package com.adarshsingh.antidistraction.worker

import com.adarshsingh.antidistraction.data.local.entity.ScheduleEntity
import com.adarshsingh.antidistraction.domain.model.FocusMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ScheduleAutomationTest {

    @Test
    fun scheduleEntity_toggleEnabledInvertsState() {
        val schedule = ScheduleEntity(
            name = "Night Focus",
            profileId = 1L,
            mode = FocusMode.DEEP_FOCUS,
            daysOfWeekMask = 127,
            startMinuteOfDay = 1380, // 23:00
            endMinuteOfDay = 420, // 07:00
            isEnabled = true
        )
        val toggled = schedule.copy(isEnabled = !schedule.isEnabled)
        assertTrue(schedule.isEnabled)
        assertEquals(false, toggled.isEnabled)
    }
}
