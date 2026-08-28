package com.adarshsingh.antidistraction.domain.engine

import com.adarshsingh.antidistraction.domain.model.BreakMode
import com.adarshsingh.antidistraction.domain.model.BreakState
import com.adarshsingh.antidistraction.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BreakEngine @Inject constructor() {
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _breakState = MutableStateFlow(BreakState())
    val breakState: StateFlow<BreakState> = _breakState.asStateFlow()

    private var breakJob: Job? = null

    fun startBreak(durationMinutes: Int = 5) {
        breakJob?.cancel()
        val durationSecs = durationMinutes * 60L
        _breakState.value = _breakState.value.copy(
            isBreakActive = true,
            breakDurationMinutes = durationMinutes,
            remainingBreakSeconds = durationSecs
        )

        breakJob = scope.launch {
            var remaining = durationSecs
            while (isActive && remaining > 0) {
                delay(1000L)
                remaining--
                _breakState.value = _breakState.value.copy(remainingBreakSeconds = remaining)
            }
            endBreak()
        }
        Logger.i("BreakEngine", "Started break for $durationMinutes minutes.")
    }

    fun endBreak() {
        breakJob?.cancel()
        _breakState.value = _breakState.value.copy(
            isBreakActive = false,
            remainingBreakSeconds = 0L,
            completedWorkCycles = _breakState.value.completedWorkCycles + 1
        )
        Logger.i("BreakEngine", "Break ended. Total cycles completed: ${_breakState.value.completedWorkCycles}")
    }

    fun setBreakMode(mode: BreakMode) {
        val duration = when (mode) {
            BreakMode.POMODORO_25_5 -> 5
            BreakMode.LONG_CYCLE_50_10 -> 10
            BreakMode.DEEP_CYCLE_90_15 -> 15
            BreakMode.NO_BREAK -> 0
        }
        _breakState.value = _breakState.value.copy(
            breakMode = mode,
            breakDurationMinutes = duration
        )
    }
}
