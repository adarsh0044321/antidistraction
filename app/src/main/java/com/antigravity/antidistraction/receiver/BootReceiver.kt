package com.antigravity.antidistraction.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.antigravity.antidistraction.domain.engine.FocusSessionEngine
import com.antigravity.antidistraction.domain.engine.ScheduleEngine
import com.antigravity.antidistraction.util.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var sessionEngine: FocusSessionEngine

    @Inject
    lateinit var scheduleEngine: ScheduleEngine

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Logger.i("BootReceiver", "Reboot complete. Restoring active focus sessions & schedules.")
            CoroutineScope(Dispatchers.Default).launch {
                sessionEngine.recoverActiveSession()
                scheduleEngine.checkAndApplyActiveSchedules()
            }
        }
    }
}
