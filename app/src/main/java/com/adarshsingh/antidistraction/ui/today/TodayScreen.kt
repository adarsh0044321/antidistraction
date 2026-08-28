package com.adarshsingh.antidistraction.ui.today

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adarshsingh.antidistraction.data.local.entity.DailyGoalEntity
import com.adarshsingh.antidistraction.ui.components.CalmButton
import com.adarshsingh.antidistraction.ui.components.CalmButtonVariant
import com.adarshsingh.antidistraction.ui.components.CalmCard
import com.adarshsingh.antidistraction.ui.components.CalmChip
import com.adarshsingh.antidistraction.ui.components.CalmDialog
import com.adarshsingh.antidistraction.ui.components.CalmProgressIndicator
import com.adarshsingh.antidistraction.ui.components.CalmTopBar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TodayScreen(
    viewModel: TodayViewModel,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = false,
    onToggleDarkMode: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddGoalDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CalmTopBar(
                title = "Today's Plan & Goals",
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary Card
            item {
                CalmCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Daily Progress",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val completedCount = uiState.goals.count { it.isCompleted }
                            Text(
                                text = "$completedCount of ${uiState.goals.size} goals completed",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                        CalmButton(
                            text = "+ Goal",
                            onClick = { showAddGoalDialog = true },
                            variant = CalmButtonVariant.PRIMARY
                        )
                    }
                }
            }

            // Goals List
            item {
                Text(
                    text = "Goals for Today",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            if (uiState.goals.isEmpty()) {
                item {
                    CalmCard {
                        Text(
                            text = "No goals added for today yet. Tap '+ Goal' above to define what you want to accomplish!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(uiState.goals, key = { it.id }) { goal ->
                    GoalCard(
                        goal = goal,
                        onToggleCompleted = { viewModel.toggleGoalCompleted(goal) }
                    )
                }
            }
        }
    }

    if (showAddGoalDialog) {
        var goalTitle by remember { mutableStateOf("") }
        var goalDurationMins by remember { mutableStateOf(60) }

        val recommendations = listOf(
            "📚 Study Java",
            "💻 Coding Project",
            "📖 Read 20 Pages",
            "🏋️ Workout",
            "🧘 Meditate"
        )

        CalmDialog(
            title = "Add Today's Goal",
            message = "Enter your goal title or select a recommended goal below:",
            confirmText = "Save Goal",
            dismissText = "Cancel",
            onConfirm = {
                if (goalTitle.isNotBlank()) {
                    viewModel.addGoal(goalTitle.trim(), goalDurationMins)
                    showAddGoalDialog = false
                }
            },
            onDismiss = { showAddGoalDialog = false }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Interactive Text Input
                OutlinedTextField(
                    value = goalTitle,
                    onValueChange = { goalTitle = it },
                    label = { Text("Goal Title") },
                    placeholder = { Text("e.g. Study Java, Reading...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Recommendations Label & Flow Chips
                Text(
                    text = "Suggested Goals:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    recommendations.forEach { rec ->
                        CalmChip(
                            text = rec,
                            isSelected = goalTitle == rec,
                            onClick = { goalTitle = rec }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Target Duration Selector
                Text(
                    text = "Target Duration:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(30, 45, 60, 90, 120).forEach { mins ->
                        val label = if (mins >= 60) "${mins / 60}h" else "${mins}m"
                        CalmChip(
                            text = label,
                            isSelected = goalDurationMins == mins,
                            onClick = { goalDurationMins = mins }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GoalCard(
    goal: DailyGoalEntity,
    onToggleCompleted: () -> Unit
) {
    CalmCard {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = goal.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val targetMins = goal.targetDurationMs / (60 * 1000)
                    val completedMins = goal.completedDurationMs / (60 * 1000)
                    Text(
                        text = "$completedMins m / $targetMins m completed • ${goal.category}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                CalmButton(
                    text = if (goal.isCompleted) "Done ✓" else "Mark Done",
                    onClick = onToggleCompleted,
                    variant = if (goal.isCompleted) CalmButtonVariant.SECONDARY else CalmButtonVariant.PRIMARY
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            val progress = if (goal.targetDurationMs > 0) (goal.completedDurationMs.toFloat() / goal.targetDurationMs.toFloat()).coerceIn(0f, 1f) else 0f
            CalmProgressIndicator(progressFraction = progress)
        }
    }
}
