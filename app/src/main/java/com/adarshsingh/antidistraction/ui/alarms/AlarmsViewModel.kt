package com.adarshsingh.antidistraction.ui.alarms

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adarshsingh.antidistraction.data.local.dao.WakeAlarmDao
import com.adarshsingh.antidistraction.data.local.entity.WakeAlarmEntity
import com.adarshsingh.antidistraction.receiver.WakeAlarmReceiver
import com.adarshsingh.antidistraction.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class AlarmsUiState(
    val alarms: List<WakeAlarmEntity> = emptyList()
)

@HiltViewModel
class AlarmsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmDao: WakeAlarmDao
) : ViewModel() {

    val uiState: StateFlow<AlarmsUiState> = alarmDao.getAllAlarmsFlow()
        .map { AlarmsUiState(alarms = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = AlarmsUiState()
        )

    fun saveAlarm(hour: Int, minute: Int, bedtimeHour: Int = 23, bedtimeMinute: Int = 0) {
        viewModelScope.launch {
            val alarm = WakeAlarmEntity(
                title = "Morning Focus Wake Alarm",
                timeHour = hour,
                timeMinute = minute,
                plannedBedtimeHour = bedtimeHour,
                plannedBedtimeMinute = bedtimeMinute,
                isEnabled = true
            )
            val id = alarmDao.insertAlarm(alarm)
            scheduleExactWakeAlarm(context, id, hour, minute)
        }
    }

    fun toggleAlarm(alarm: WakeAlarmEntity) {
        viewModelScope.launch {
            val updated = alarm.copy(isEnabled = !alarm.isEnabled)
            alarmDao.updateAlarm(updated)
            if (updated.isEnabled) {
                scheduleExactWakeAlarm(context, updated.id, updated.timeHour, updated.timeMinute)
            } else {
                cancelWakeAlarm(context, updated.id)
            }
        }
    }

    private fun scheduleExactWakeAlarm(context: Context, alarmId: Long, hour: Int, minute: Int) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, WakeAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                alarmId.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                if (before(Calendar.getInstance())) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent)
            }
            Logger.i("AlarmsViewModel", "Scheduled wake alarm for $hour:$minute")
        } catch (e: Exception) {
            Logger.e("AlarmsViewModel", "Failed to schedule exact alarm: ${e.message}")
        }
    }

    private fun cancelWakeAlarm(context: Context, alarmId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, WakeAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }
}
