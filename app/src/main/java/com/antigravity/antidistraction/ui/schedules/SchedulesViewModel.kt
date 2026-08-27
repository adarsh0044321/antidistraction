package com.antigravity.antidistraction.ui.schedules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antigravity.antidistraction.data.local.dao.ScheduleDao
import com.antigravity.antidistraction.data.local.entity.ScheduleEntity
import com.antigravity.antidistraction.domain.model.FocusMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SchedulesUiState(
    val schedules: List<ScheduleEntity> = emptyList()
)

@HiltViewModel
class SchedulesViewModel @Inject constructor(
    private val scheduleDao: ScheduleDao
) : ViewModel() {

    val uiState: StateFlow<SchedulesUiState> = scheduleDao.getAllSchedulesFlow().map { list ->
        SchedulesUiState(schedules = list)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = SchedulesUiState()
    )

    fun addSchedule(name: String, mode: FocusMode, startMin: Int, endMin: Int) {
        viewModelScope.launch {
            val entity = ScheduleEntity(
                name = name,
                profileId = 1L,
                mode = mode,
                daysOfWeekMask = 31, // Mon-Fri default
                startMinuteOfDay = startMin,
                endMinuteOfDay = endMin,
                isEnabled = true
            )
            scheduleDao.insertSchedule(entity)
        }
    }

    fun toggleSchedule(schedule: ScheduleEntity) {
        viewModelScope.launch {
            scheduleDao.updateSchedule(schedule.copy(isEnabled = !schedule.isEnabled))
        }
    }

    fun deleteSchedule(schedule: ScheduleEntity) {
        viewModelScope.launch {
            scheduleDao.deleteSchedule(schedule)
        }
    }
}
