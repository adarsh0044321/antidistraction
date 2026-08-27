package com.antigravity.antidistraction.ui.intervention

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antigravity.antidistraction.domain.engine.InterventionEngine
import com.antigravity.antidistraction.domain.engine.RestrictionEngine
import com.antigravity.antidistraction.domain.model.IntentionType
import com.antigravity.antidistraction.domain.model.InterventionLevel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InterventionViewModel @Inject constructor(
    private val interventionEngine: InterventionEngine,
    private val restrictionEngine: RestrictionEngine
) : ViewModel() {

    fun recordReturnedToFocus(packageName: String, intention: IntentionType?) {
        viewModelScope.launch {
            interventionEngine.recordAttempt(
                packageName = packageName,
                intention = intention,
                level = InterventionLevel.LEVEL_5,
                bypassGranted = false,
                userAction = "RETURNED_TO_FOCUS"
            )
        }
    }

    fun recordBypassGranted(packageName: String, intention: IntentionType?, durationMinutes: Int = 2) {
        restrictionEngine.grantTemporaryException(
            packageName = packageName,
            durationMinutes = durationMinutes,
            reason = intention?.displayName ?: "Temporary Exception"
        )

        viewModelScope.launch {
            interventionEngine.recordAttempt(
                packageName = packageName,
                intention = intention,
                level = InterventionLevel.LEVEL_5,
                bypassGranted = true,
                userAction = "BYPASSED"
            )
        }
    }
}
