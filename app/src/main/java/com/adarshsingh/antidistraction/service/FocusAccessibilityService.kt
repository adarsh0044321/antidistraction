package com.adarshsingh.antidistraction.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityEvent
import com.adarshsingh.antidistraction.domain.engine.FocusSessionEngine
import com.adarshsingh.antidistraction.domain.engine.InterventionEngine
import com.adarshsingh.antidistraction.domain.model.RestrictionDecision
import com.adarshsingh.antidistraction.ui.intervention.InterventionActivity
import com.adarshsingh.antidistraction.util.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FocusAccessibilityService : AccessibilityService() {

    @Inject
    lateinit var sessionEngine: FocusSessionEngine

    @Inject
    lateinit var interventionEngine: InterventionEngine

    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private var lastInterventionTimestamp: Long = 0L

    private val systemUiPackages = setOf(
        "com.android.systemui",
        "com.google.android.apps.nexuslauncher",
        "com.sec.android.app.launcher",
        "com.miui.home",
        "com.huawei.android.launcher",
        "com.oppo.launcher",
        "com.oneplus.launcher",
        "com.android.launcher",
        "com.google.android.inputmethod.latin",
        "com.samsung.android.honeyboard"
    )

    override fun onServiceConnected() {
        super.onServiceConnected()
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS or AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
            notificationTimeout = 50
        }
        this.serviceInfo = info
        Logger.i("FocusAccessibility", "FocusAccessibilityService CONNECTED & ACTIVE!")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return
            if (packageName == this.packageName) return // Ignore self
            if (systemUiPackages.contains(packageName)) return // Ignore system UI / Launchers

            handlePackageWindowChange(packageName)
        }
    }

    private fun handlePackageWindowChange(packageName: String) {
        val now = System.currentTimeMillis()
        if (now - lastInterventionTimestamp < 1000L) {
            return // 1-second debounce to prevent double triggers on fast window events
        }

        serviceScope.launch {
            if (!sessionEngine.isSessionActive()) {
                return@launch
            }

            val interventionResult = interventionEngine.processIntervention(packageName)

            when (interventionResult.decision) {
                RestrictionDecision.RESTRICTED, RestrictionDecision.WARN, RestrictionDecision.DELAY -> {
                    lastInterventionTimestamp = System.currentTimeMillis()
                    Logger.i("FocusAccessibility", "Intercepted restricted package $packageName (${interventionResult.decision.name}). Triggering Intervention screen.")

                    // Perform Home Action to step out of target app immediately
                    performGlobalAction(GLOBAL_ACTION_HOME)

                    // Launch Intervention Screen
                    InterventionActivity.start(this@FocusAccessibilityService, packageName)
                }
                RestrictionDecision.ALLOWED, RestrictionDecision.EMERGENCY_ALLOWED, RestrictionDecision.TEMPORARY_EXCEPTION_ALLOWED -> {
                    // Allowed apps pass through cleanly
                }
            }
        }
    }

    override fun onInterrupt() {
        Logger.w("FocusAccessibility", "Accessibility Service interrupted.")
    }
}
