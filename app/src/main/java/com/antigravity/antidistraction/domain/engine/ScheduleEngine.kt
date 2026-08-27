package com.antigravity.antidistraction.domain.engine

import com.antigravity.antidistraction.data.local.dao.ScheduleDao
import com.antigravity.antidistraction.data.local.entity.ScheduleEntity
import com.antigravity.antidistraction.domain.model.FocusMode
import com.antigravity.antidistraction.util.Logger
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduleEngine @Inject constructor(
    private val scheduleDao: ScheduleDao,
    private val sessionEngine: FocusSessionEngine
) {

    suspend fun checkAndApplyActiveSchedules() {
        val activeSchedules = scheduleDao.getActiveSchedules()
        val calendar = Calendar.getInstance()

        val currentDayBit = getDayBitmask(calendar.get(Calendar.DAY_OF_WEEK))
        val currentMinuteOfDay = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)

        val matchingSchedule = activeSchedules.firstOrNull { schedule ->
            val dayMatches = (schedule.daysOfWeekMask and currentDayBit) != 0
            val timeMatches = if (schedule.startMinuteOfDay <= schedule.endMinuteOfDay) {
                currentMinuteOfDay in schedule.startMinuteOfDay..schedule.endMinuteOfDay
            } else {
                // Overnight schedule (e.g. 23:00 to 07:00)
                currentMinuteOfDay >= schedule.startMinuteOfDay || currentMinuteOfDay <= schedule.endMinuteOfDay
            }
            dayMatches && timeMatches
        }

        if (matchingSchedule != null) {
            val sessionState = sessionEngine.sessionState.value
            if (sessionState.state == com.antigravity.antidistraction.domain.model.FocusState.IDLE) {
                val remainingMins = calculateRemainingMinutes(matchingSchedule, currentMinuteOfDay)
                sessionEngine.startSession(remainingMins, matchingSchedule.mode)
                Logger.i("ScheduleEngine", "Applied scheduled focus session: ${matchingSchedule.name} ($remainingMins mins remaining)")
            }
        }
    }

    private fun calculateRemainingMinutes(schedule: ScheduleEntity, currentMinute: Int): Int {
        return if (schedule.startMinuteOfDay <= schedule.endMinuteOfDay) {
            maxOf(1, schedule.endMinuteOfDay - currentMinute)
        } else {
            if (currentMinute >= schedule.startMinuteOfDay) {
                maxOf(1, (1440 - currentMinute) + schedule.endMinuteOfDay)
            } else {
                maxOf(1, schedule.endMinuteOfDay - currentMinute)
            }
        }
    }

    private fun getDayBitmask(calendarDayOfWeek: Int): Int {
        return when (calendarDayOfWeek) {
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 4
            Calendar.THURSDAY -> 8
            Calendar.FRIDAY -> 16
            Calendar.SATURDAY -> 32
            Calendar.SUNDAY -> 64
            else -> 0
        }
    }
}
