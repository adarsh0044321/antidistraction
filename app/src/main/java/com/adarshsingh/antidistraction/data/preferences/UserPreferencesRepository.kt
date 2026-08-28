package com.adarshsingh.antidistraction.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.adarshsingh.antidistraction.domain.model.FocusState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class ActiveExceptionInfo(
    val packageName: String,
    val expirationMs: Long
)

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        val ACTIVE_PROFILE_ID = longPreferencesKey("active_profile_id")
        val CURRENT_FOCUS_STATE = stringPreferencesKey("current_focus_state")
        val NOTIFICATION_SUPPRESSION_ENABLED = booleanPreferencesKey("notification_suppression_enabled")
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val ACTIVE_EXCEPTION_PACKAGE = stringPreferencesKey("active_exception_package")
        val ACTIVE_EXCEPTION_EXPIRATION_MS = longPreferencesKey("active_exception_expiration_ms")
    }

    val isDarkModeFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[Keys.IS_DARK_MODE] ?: false
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.IS_DARK_MODE] = enabled
        }
    }

    val isFirstLaunchFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[Keys.IS_FIRST_LAUNCH] ?: true
    }

    suspend fun setFirstLaunchCompleted() {
        context.dataStore.edit { preferences ->
            preferences[Keys.IS_FIRST_LAUNCH] = false
        }
    }

    val activeProfileIdFlow: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[Keys.ACTIVE_PROFILE_ID] ?: 1L
    }

    suspend fun setActiveProfileId(profileId: Long) {
        context.dataStore.edit { preferences ->
            preferences[Keys.ACTIVE_PROFILE_ID] = profileId
        }
    }

    val currentFocusStateFlow: Flow<FocusState> = context.dataStore.data.map { preferences ->
        val name = preferences[Keys.CURRENT_FOCUS_STATE] ?: FocusState.IDLE.name
        try {
            FocusState.valueOf(name)
        } catch (e: Exception) {
            FocusState.IDLE
        }
    }

    suspend fun setCurrentFocusState(state: FocusState) {
        context.dataStore.edit { preferences ->
            preferences[Keys.CURRENT_FOCUS_STATE] = state.name
        }
    }

    val activeExceptionFlow: Flow<ActiveExceptionInfo?> = context.dataStore.data.map { preferences ->
        val pkg = preferences[Keys.ACTIVE_EXCEPTION_PACKAGE] ?: return@map null
        val exp = preferences[Keys.ACTIVE_EXCEPTION_EXPIRATION_MS] ?: 0L
        if (pkg.isNotEmpty() && exp > System.currentTimeMillis()) {
            ActiveExceptionInfo(pkg, exp)
        } else {
            null
        }
    }

    suspend fun setActiveException(packageName: String, durationMs: Long = 120_000L) {
        context.dataStore.edit { preferences ->
            preferences[Keys.ACTIVE_EXCEPTION_PACKAGE] = packageName
            preferences[Keys.ACTIVE_EXCEPTION_EXPIRATION_MS] = System.currentTimeMillis() + durationMs
        }
    }

    suspend fun clearActiveException() {
        context.dataStore.edit { preferences ->
            preferences.remove(Keys.ACTIVE_EXCEPTION_PACKAGE)
            preferences.remove(Keys.ACTIVE_EXCEPTION_EXPIRATION_MS)
        }
    }
}
