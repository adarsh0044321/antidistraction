package com.adarshsingh.antidistraction.ui.rules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adarshsingh.antidistraction.data.preferences.UserPreferencesRepository
import com.adarshsingh.antidistraction.domain.engine.RestrictionEngine
import com.adarshsingh.antidistraction.domain.model.TemporaryException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RulesUiState(
    val activeExceptions: List<TemporaryException> = emptyList()
)

@HiltViewModel
class RulesViewModel @Inject constructor(
    private val restrictionEngine: RestrictionEngine,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RulesUiState())
    val uiState: StateFlow<RulesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            while (isActive) {
                refreshExceptions()
                delay(1000L)
            }
        }
    }

    fun refreshExceptions() {
        val exceptions = restrictionEngine.getActiveExceptions()
        _uiState.value = RulesUiState(activeExceptions = exceptions)
    }

    fun revokeException(packageName: String) {
        restrictionEngine.revokeTemporaryException(packageName)
        viewModelScope.launch {
            userPreferencesRepository.clearActiveException()
            refreshExceptions()
        }
    }
}
