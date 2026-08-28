package com.adarshsingh.antidistraction.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adarshsingh.antidistraction.data.local.dao.FocusProfileDao
import com.adarshsingh.antidistraction.data.local.entity.FocusProfileEntity
import com.adarshsingh.antidistraction.data.preferences.UserPreferencesRepository
import com.adarshsingh.antidistraction.domain.model.FocusMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val currentStep: Int = 1,
    val totalSteps: Int = 4,
    val selectedDistractions: Set<String> = setOf("Social Media", "Short Videos", "Games"),
    val selectedFocusMode: FocusMode = FocusMode.DEEP_FOCUS,
    val isUsageAccessGranted: Boolean = false,
    val isAccessibilityGranted: Boolean = false,
    val isOverlayGranted: Boolean = false,
    val isNotificationGranted: Boolean = false,
    val isOnboardingComplete: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val focusProfileDao: FocusProfileDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun toggleDistraction(category: String) {
        val current = _uiState.value.selectedDistractions.toMutableSet()
        if (current.contains(category)) {
            current.remove(category)
        } else {
            current.add(category)
        }
        _uiState.value = _uiState.value.copy(selectedDistractions = current)
    }

    fun setFocusMode(mode: FocusMode) {
        _uiState.value = _uiState.value.copy(selectedFocusMode = mode)
    }

    fun updatePermissionStates(
        usage: Boolean,
        accessibility: Boolean,
        overlay: Boolean,
        notification: Boolean
    ) {
        _uiState.value = _uiState.value.copy(
            isUsageAccessGranted = usage,
            isAccessibilityGranted = accessibility,
            isOverlayGranted = overlay,
            isNotificationGranted = notification
        )
    }

    fun nextStep() {
        if (_uiState.value.currentStep < _uiState.value.totalSteps) {
            _uiState.value = _uiState.value.copy(currentStep = _uiState.value.currentStep + 1)
        } else {
            completeOnboarding()
        }
    }

    fun previousStep() {
        if (_uiState.value.currentStep > 1) {
            _uiState.value = _uiState.value.copy(currentStep = _uiState.value.currentStep - 1)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            // Save initial default focus profile
            val defaultProfile = FocusProfileEntity(
                name = _uiState.value.selectedFocusMode.name.replace("_", " "),
                mode = _uiState.value.selectedFocusMode,
                isDefault = true,
                workDurationMinutes = 25,
                breakDurationMinutes = 5
            )
            val profileId = focusProfileDao.insertProfile(defaultProfile)
            userPreferencesRepository.setActiveProfileId(profileId)
            userPreferencesRepository.setFirstLaunchCompleted()
            _uiState.value = _uiState.value.copy(isOnboardingComplete = true)
        }
    }
}
