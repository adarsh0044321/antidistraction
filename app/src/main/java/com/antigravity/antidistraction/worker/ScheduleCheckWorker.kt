package com.antigravity.antidistraction.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.antigravity.antidistraction.domain.engine.ScheduleEngine
import com.antigravity.antidistraction.util.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ScheduleCheckWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val scheduleEngine: ScheduleEngine
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Logger.i("ScheduleCheckWorker", "WorkManager evaluating active schedule rules.")
            scheduleEngine.checkAndApplyActiveSchedules()
            Result.success()
        } catch (e: Exception) {
            Logger.e("ScheduleCheckWorker", "Error evaluating schedules in background worker", e)
            Result.retry()
        }
    }
}
