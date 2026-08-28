package com.adarshsingh.antidistraction.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.adarshsingh.antidistraction.data.local.dao.NotificationEventDao
import com.adarshsingh.antidistraction.data.local.entity.NotificationEventEntity
import com.adarshsingh.antidistraction.domain.engine.FocusSessionEngine
import com.adarshsingh.antidistraction.domain.model.FocusState
import com.adarshsingh.antidistraction.domain.repository.AppRestrictionRepository
import com.adarshsingh.antidistraction.util.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FocusNotificationListenerService : NotificationListenerService() {

    @Inject
    lateinit var sessionEngine: FocusSessionEngine

    @Inject
    lateinit var appRestrictionRepository: AppRestrictionRepository

    @Inject
    lateinit var notificationEventDao: NotificationEventDao

    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return
        val packageName = sbn.packageName ?: return
        if (packageName == this.packageName) return // Ignore self

        val sessionState = sessionEngine.sessionState.value.state
        if (sessionState != FocusState.FOCUS_ACTIVE && sessionState != FocusState.RESUMED) {
            return
        }

        serviceScope.launch {
            val isEmergency = appRestrictionRepository.isAppEmergency(packageName)
            if (isEmergency) return@launch

            val isRestricted = appRestrictionRepository.isAppRestricted(packageName)
            if (isRestricted) {
                val extras = sbn.notification.extras
                val title = extras.getString("android.title")
                val text = extras.getCharSequence("android.text")?.toString()

                // Cancel notification to suppress sound/vibration/popup
                cancelNotification(sbn.key)

                // Save locally for Post-Focus Digest
                val event = NotificationEventEntity(
                    packageName = packageName,
                    title = title,
                    text = text,
                    postTimeMs = sbn.postTime,
                    isSuppressed = true
                )
                notificationEventDao.insertNotification(event)

                Logger.i("FocusNotificationListener", "Suppressed notification from $packageName: $title")
            }
        }
    }
}
