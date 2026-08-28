package com.adarshsingh.antidistraction.ui.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adarshsingh.antidistraction.data.local.dao.DailyGoalDao
import com.adarshsingh.antidistraction.data.local.entity.DailyGoalEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class TodayUiState(
    val goals: List<DailyGoalEntity> = emptyList(),
    val todayDateStr: String = ""
)

@HiltViewModel
class TodayViewModel @Inject constructor(
    private val goalDao: DailyGoalDao
) : ViewModel() {

    private val todayDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    val uiState: StateFlow<TodayUiState> = goalDao.getGoalsForDateFlow(todayDateStr)
        .map { goals ->
            TodayUiState(goals = goals, todayDateStr = todayDateStr)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = TodayUiState(todayDateStr = todayDateStr)
        )

    fun addGoal(title: String, durationMinutes: Int, category: String = "GENERAL", priority: String = "MEDIUM") {
        viewModelScope.launch {
            val goal = DailyGoalEntity(
                title = title,
                targetDurationMs = durationMinutes * 60 * 1000L,
                category = category,
                priority = priority,
                createdDateStr = todayDateStr
            )
            goalDao.insertGoal(goal)
        }
    }

    fun toggleGoalCompleted(goal: DailyGoalEntity) {
        viewModelScope.launch {
            goalDao.updateGoal(goal.copy(isCompleted = !goal.isCompleted))
        }
    }
}
