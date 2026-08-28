package com.adarshsingh.antidistraction.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adarshsingh.antidistraction.data.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val isFirstLaunch: StateFlow<Boolean> = userPreferencesRepository.isFirstLaunchFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = true
    )

    val isDarkMode: StateFlow<Boolean> = userPreferencesRepository.isDarkModeFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = false
    )

    fun toggleDarkMode() {
        viewModelScope.launch {
            userPreferencesRepository.setDarkMode(!isDarkMode.value)
        }
    }
}
