package com.adarshsingh.antidistraction.domain.permission

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.text.TextUtils
import com.adarshsingh.antidistraction.service.FocusAccessibilityService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

enum class ProtectionLevel {
    FULL_PROTECTION,
    LIMITED_PROTECTION,
    NO_PROTECTION
}

enum class CapabilityStatus {
    GRANTED,
    DENIED,
    RESTRICTED_BY_ANDROID,
    REVOKED,
    UNAVAILABLE,
    NOT_REQUIRED
}

data class PermissionState(
    val isUsageAccessGranted: Boolean,
    val isAccessibilityGranted: Boolean,
    val isOverlayGranted: Boolean,
    val isNotificationGranted: Boolean,
    val accessibilityStatus: CapabilityStatus = CapabilityStatus.DENIED,
    val usageStatus: CapabilityStatus = CapabilityStatus.DENIED,
    val protectionLevel: ProtectionLevel
)

@Singleton
open class PermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    open fun getPermissionState(): PermissionState {
        val usage = isUsageAccessGranted()
        val accessibility = isAccessibilityGranted()
        val overlay = isOverlayGranted()
        val notification = isNotificationGranted()

        val accessibilityStatus = if (accessibility) CapabilityStatus.GRANTED else CapabilityStatus.DENIED
        val usageStatus = if (usage) CapabilityStatus.GRANTED else CapabilityStatus.DENIED

        val level = when {
            accessibility && usage && overlay -> ProtectionLevel.FULL_PROTECTION
            accessibility || usage -> ProtectionLevel.LIMITED_PROTECTION
            else -> ProtectionLevel.NO_PROTECTION
        }

        return PermissionState(
            isUsageAccessGranted = usage,
            isAccessibilityGranted = accessibility,
            isOverlayGranted = overlay,
            isNotificationGranted = notification,
            accessibilityStatus = accessibilityStatus,
            usageStatus = usageStatus,
            protectionLevel = level
        )
    }

    fun isUsageAccessGranted(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager ?: return false
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun isAccessibilityGranted(): Boolean {
        val expectedComponentName = "${context.packageName}/${FocusAccessibilityService::class.java.canonicalName}"
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false

        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServices)
        while (colonSplitter.hasNext()) {
            val componentName = colonSplitter.next()
            if (componentName.equals(expectedComponentName, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    fun isOverlayGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }

    fun isNotificationGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}
