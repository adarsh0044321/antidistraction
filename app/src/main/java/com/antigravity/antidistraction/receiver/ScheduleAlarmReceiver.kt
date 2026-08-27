package com.antigravity.antidistraction.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.antigravity.antidistraction.domain.engine.ScheduleEngine
import com.antigravity.antidistraction.util.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ScheduleAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var scheduleEngine: ScheduleEngine

    override fun onReceive(context: Context?, intent: Intent?) {
        Logger.i("ScheduleAlarmReceiver", "Exact Alarm trigger received. Evaluating schedules.")
        CoroutineScope(Dispatchers.Default).launch {
            scheduleEngine.checkAndApplyActiveSchedules()
        }
    }
}
