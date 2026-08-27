package com.antigravity.antidistraction.ui.rules

import androidx.lifecycle.ViewModel
import com.antigravity.antidistraction.domain.engine.RestrictionEngine
import com.antigravity.antidistraction.domain.model.TemporaryException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class RulesUiState(
    val activeExceptions: List<TemporaryException> = emptyList()
)

@HiltViewModel
class RulesViewModel @Inject constructor(
    private val restrictionEngine: RestrictionEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow(RulesUiState())
    val uiState: StateFlow<RulesUiState> = _uiState.asStateFlow()

    init {
        refreshExceptions()
    }

    fun refreshExceptions() {
        _uiState.value = RulesUiState(activeExceptions = restrictionEngine.getActiveExceptions())
    }

    fun revokeException(packageName: String) {
        restrictionEngine.revokeTemporaryException(packageName)
        refreshExceptions()
    }
}
