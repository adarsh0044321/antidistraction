package com.antigravity.antidistraction.domain.compatibility

import android.os.Build
import com.antigravity.antidistraction.domain.model.OemGuide
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OemCompatibilityManager @Inject constructor() {

    fun detectOemGuide(): OemGuide {
        val manufacturer = Build.MANUFACTURER.lowercase()

        return when {
            manufacturer.contains("xiaomi") || manufacturer.contains("redmi") || manufacturer.contains("poco") -> {
                OemGuide(
                    manufacturerName = "Xiaomi / MIUI / HyperOS",
                    requiresAutostart = true,
                    requiresBatteryExemption = true,
                    setupInstructions = listOf(
                        "1. Open Device Settings -> Apps -> Manage Apps.",
                        "2. Select Anti-Distraction and turn ON 'Autostart'.",
                        "3. Set Battery Saver to 'No restrictions'."
                    )
                )
            }
            manufacturer.contains("samsung") -> {
                OemGuide(
                    manufacturerName = "Samsung One UI",
                    requiresAutostart = false,
                    requiresBatteryExemption = true,
                    setupInstructions = listOf(
                        "1. Open Device Settings -> Device Care -> Battery.",
                        "2. Tap Background usage limits -> Never sleeping apps.",
                        "3. Add Anti-Distraction to the list."
                    )
                )
            }
            manufacturer.contains("oneplus") || manufacturer.contains("oppo") || manufacturer.contains("realme") -> {
                OemGuide(
                    manufacturerName = "OnePlus / OPPO / Realme",
                    requiresAutostart = true,
                    requiresBatteryExemption = true,
                    setupInstructions = listOf(
                        "1. Open App Info for Anti-Distraction.",
                        "2. Enable 'Allow auto-launch' and 'Allow background activity'."
                    )
                )
            }
            else -> {
                OemGuide(
                    manufacturerName = Build.MANUFACTURER.uppercase(),
                    requiresAutostart = false,
                    requiresBatteryExemption = true,
                    setupInstructions = listOf(
                        "1. Disable battery optimization for Anti-Distraction in System Settings."
                    )
                )
            }
        }
    }
}
