package com.adarshsingh.antidistraction.ui.focus

import androidx.lifecycle.ViewModel
import com.adarshsingh.antidistraction.domain.engine.FocusSessionEngine
import com.adarshsingh.antidistraction.domain.model.FocusMode
import com.adarshsingh.antidistraction.domain.model.FocusSessionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class FocusViewModel @Inject constructor(
    private val sessionEngine: FocusSessionEngine
) : ViewModel() {

    val sessionState: StateFlow<FocusSessionState> = sessionEngine.sessionState

    fun startSession(durationMinutes: Int = 25, mode: FocusMode = FocusMode.DEEP_FOCUS) {
        sessionEngine.startSession(durationMinutes, mode)
    }

    fun pauseSession() {
        sessionEngine.pauseSession()
    }

    fun resumeSession() {
        sessionEngine.resumeSession()
    }

    fun completeSession() {
        sessionEngine.completeSession()
    }

    fun abandonSession() {
        sessionEngine.abandonSession()
    }
}
