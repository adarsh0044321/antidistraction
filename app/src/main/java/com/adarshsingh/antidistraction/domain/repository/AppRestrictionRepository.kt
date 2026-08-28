package com.adarshsingh.antidistraction.domain.repository

import com.adarshsingh.antidistraction.domain.model.AppInfo
import kotlinx.coroutines.flow.Flow

interface AppRestrictionRepository {
    suspend fun discoverInstalledApps(): List<AppInfo>
    fun getSavedAppsFlow(): Flow<List<AppInfo>>
    suspend fun setAppBlockedState(packageName: String, appName: String, category: String, isBlocked: Boolean)
    suspend fun setEmergencyApp(packageName: String, appName: String, category: String, isEmergency: Boolean)
    suspend fun isAppEmergency(packageName: String): Boolean
    suspend fun isAppRestricted(packageName: String): Boolean
}
