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

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        val ACTIVE_PROFILE_ID = longPreferencesKey("active_profile_id")
        val CURRENT_FOCUS_STATE = stringPreferencesKey("current_focus_state")
        val NOTIFICATION_SUPPRESSION_ENABLED = booleanPreferencesKey("notification_suppression_enabled")
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
}
