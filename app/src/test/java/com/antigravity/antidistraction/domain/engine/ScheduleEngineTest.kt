package com.antigravity.antidistraction.domain.engine

import com.antigravity.antidistraction.data.local.entity.ScheduleEntity
import com.antigravity.antidistraction.domain.model.FocusMode
import org.junit.Assert.assertEquals
import org.junit.Test

class ScheduleEngineTest {

    @Test
    fun scheduleEntity_bitmaskCreationIsCorrect() {
        val schedule = ScheduleEntity(
            name = "Work Hours",
            profileId = 1L,
            mode = FocusMode.WORK,
            daysOfWeekMask = 31, // Mon-Fri (1+2+4+8+16)
            startMinuteOfDay = 480, // 08:00
            endMinuteOfDay = 720 // 12:00
        )
        assertEquals(31, schedule.daysOfWeekMask)
        assertEquals(480, schedule.startMinuteOfDay)
    }
}
