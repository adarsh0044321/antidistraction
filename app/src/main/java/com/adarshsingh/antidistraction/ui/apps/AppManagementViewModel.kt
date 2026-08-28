package com.adarshsingh.antidistraction.ui.apps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adarshsingh.antidistraction.domain.model.AppInfo
import com.adarshsingh.antidistraction.domain.repository.AppRestrictionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppManagementUiState(
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedCategoryFilter: String = "All",
    val allApps: List<AppInfo> = emptyList(),
    val filteredApps: List<AppInfo> = emptyList(),
    val categories: List<String> = listOf("All", "Social Media", "Streaming Video", "Games", "Communication", "Utilities", "General")
)

@HiltViewModel
class AppManagementViewModel @Inject constructor(
    private val appRestrictionRepository: AppRestrictionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppManagementUiState())
    val uiState: StateFlow<AppManagementUiState> = _uiState.asStateFlow()

    init {
        loadApps()
    }

    fun loadApps() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val apps = appRestrictionRepository.discoverInstalledApps()
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                allApps = apps
            )
            applyFilters()
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    fun setCategoryFilter(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategoryFilter = category)
        applyFilters()
    }

    fun toggleAppBlocked(app: AppInfo) {
        viewModelScope.launch {
            val newBlockedState = !app.isBlocked
            appRestrictionRepository.setAppBlockedState(app.packageName, app.appName, app.category, newBlockedState)
            loadApps()
        }
    }

    fun toggleEmergencyApp(app: AppInfo) {
        viewModelScope.launch {
            val newEmergencyState = !app.isEmergencyApp
            appRestrictionRepository.setEmergencyApp(app.packageName, app.appName, app.category, newEmergencyState)
            loadApps()
        }
    }

    private fun applyFilters() {
        val query = _uiState.value.searchQuery.trim().lowercase()
        val category = _uiState.value.selectedCategoryFilter

        val filtered = _uiState.value.allApps.filter { app ->
            val matchesQuery = query.isEmpty() || app.appName.lowercase().contains(query) || app.packageName.lowercase().contains(query)
            val matchesCategory = category == "All" || app.category == category
            matchesQuery && matchesCategory
        }

        _uiState.value = _uiState.value.copy(filteredApps = filtered)
    }
}
