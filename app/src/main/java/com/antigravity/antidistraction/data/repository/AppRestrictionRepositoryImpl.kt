package com.antigravity.antidistraction.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.antigravity.antidistraction.data.local.dao.BlockedAppDao
import com.antigravity.antidistraction.data.local.entity.BlockedAppEntity
import com.antigravity.antidistraction.domain.model.AppInfo
import com.antigravity.antidistraction.domain.repository.AppRestrictionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRestrictionRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val blockedAppDao: BlockedAppDao
) : AppRestrictionRepository {

    override suspend fun discoverInstalledApps(): List<AppInfo> {
        val pm = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = pm.queryIntentActivities(mainIntent, 0)

        val installedApps = resolveInfos.mapNotNull { resolveInfo ->
            val packageName = resolveInfo.activityInfo.packageName
            if (packageName == context.packageName) return@mapNotNull null // Exclude self

            val appName = resolveInfo.loadLabel(pm).toString()
            val isSystemApp = (resolveInfo.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

            val category = classifyCategory(packageName, isSystemApp)
            val savedEntity = blockedAppDao.getAppByPackageName(packageName)
            val isDefaultDistracting = category in listOf("Social Media", "Streaming Video", "Games")

            AppInfo(
                packageName = packageName,
                appName = appName,
                category = category,
                isBlocked = savedEntity?.userClassification == "DISTRACTING" || (savedEntity == null && isDefaultDistracting),
                isEmergencyApp = savedEntity?.isEmergencyApp ?: isKnownEmergencyApp(packageName),
                userClassification = savedEntity?.userClassification ?: if (isKnownEmergencyApp(packageName)) "PRODUCTIVE" else if (isDefaultDistracting) "DISTRACTING" else "NEUTRAL",
                distractionScore = savedEntity?.distractionScore ?: 0
            )
        }.distinctBy { it.packageName }.sortedBy { it.appName }

        return installedApps
    }

    override fun getSavedAppsFlow(): Flow<List<AppInfo>> {
        return blockedAppDao.getAllAppsFlow().map { entities ->
            entities.map { entity ->
                AppInfo(
                    packageName = entity.packageName,
                    appName = entity.appName,
                    category = entity.category,
                    isBlocked = entity.userClassification == "DISTRACTING",
                    isEmergencyApp = entity.isEmergencyApp,
                    customRestrictionLevel = entity.customRestrictionLevel,
                    userClassification = entity.userClassification,
                    distractionScore = entity.distractionScore
                )
            }
        }
    }

    override suspend fun setAppBlockedState(packageName: String, appName: String, category: String, isBlocked: Boolean) {
        val existing = blockedAppDao.getAppByPackageName(packageName)
        val entity = BlockedAppEntity(
            packageName = packageName,
            appName = appName,
            category = category,
            customRestrictionLevel = existing?.customRestrictionLevel,
            userClassification = if (isBlocked) "DISTRACTING" else "NEUTRAL",
            isEmergencyApp = existing?.isEmergencyApp ?: false,
            distractionScore = existing?.distractionScore ?: 0
        )
        blockedAppDao.insertOrUpdateApp(entity)
    }

    override suspend fun setEmergencyApp(packageName: String, appName: String, category: String, isEmergency: Boolean) {
        val existing = blockedAppDao.getAppByPackageName(packageName)
        val entity = BlockedAppEntity(
            packageName = packageName,
            appName = appName,
            category = category,
            customRestrictionLevel = existing?.customRestrictionLevel,
            userClassification = if (isEmergency) "PRODUCTIVE" else (existing?.userClassification ?: "NEUTRAL"),
            isEmergencyApp = isEmergency,
            distractionScore = existing?.distractionScore ?: 0
        )
        blockedAppDao.insertOrUpdateApp(entity)
    }

    override suspend fun isAppEmergency(packageName: String): Boolean {
        if (isKnownEmergencyApp(packageName)) return true
        val entity = blockedAppDao.getAppByPackageName(packageName)
        return entity?.isEmergencyApp ?: false
    }

    override suspend fun isAppRestricted(packageName: String): Boolean {
        if (isAppEmergency(packageName)) return false
        val entity = blockedAppDao.getAppByPackageName(packageName)
        if (entity != null) {
            return entity.userClassification == "DISTRACTING"
        }
        // Default classification for unsaved packages based on package heuristics
        val category = classifyCategory(packageName, false)
        return category in listOf("Social Media", "Streaming Video", "Games")
    }

    private fun classifyCategory(packageName: String, isSystemApp: Boolean): String {
        val lower = packageName.lowercase()
        return when {
            lower.contains("instagram") || lower.contains("facebook") || lower.contains("twitter") || lower.contains("tiktok") || lower.contains("reddit") || lower.contains("snapchat") || lower.contains("x.android") -> "Social Media"
            lower.contains("youtube") || lower.contains("netflix") || lower.contains("primevideo") || lower.contains("hulu") || lower.contains("disney") -> "Streaming Video"
            lower.contains("game") || lower.contains("pubg") || lower.contains("candycrush") || lower.contains("clash") -> "Games"
            lower.contains("whatsapp") || lower.contains("telegram") || lower.contains("signal") -> "Communication"
            lower.contains("calculator") || lower.contains("clock") || lower.contains("camera") || isSystemApp -> "Utilities"
            else -> "General"
        }
    }

    private fun isKnownEmergencyApp(packageName: String): Boolean {
        val lower = packageName.lowercase()
        return lower.contains("dialer") ||
                lower.contains("phone") ||
                lower.contains("contacts") ||
                lower.contains("emergency") ||
                lower.contains("maps") ||
                lower.contains("bank")
    }
}
