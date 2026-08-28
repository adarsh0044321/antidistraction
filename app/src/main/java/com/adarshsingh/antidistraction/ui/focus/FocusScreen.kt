package com.adarshsingh.antidistraction.ui.focus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
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
import com.adarshsingh.antidistraction.domain.model.FocusMode
import com.adarshsingh.antidistraction.domain.model.FocusState
import com.adarshsingh.antidistraction.ui.components.CalmButton
import com.adarshsingh.antidistraction.ui.components.CalmButtonVariant
import com.adarshsingh.antidistraction.ui.components.CalmCard
import com.adarshsingh.antidistraction.ui.components.CalmChip
import com.adarshsingh.antidistraction.ui.components.CalmDialog
import com.adarshsingh.antidistraction.ui.components.CalmTimerDisplay
import java.util.Locale

@Composable
fun FocusScreen(
    viewModel: FocusViewModel,
    modifier: Modifier = Modifier
) {
    val sessionState by viewModel.sessionState.collectAsState()

    // Top-level state holders for Dialogs & Controls
    var selectedDurationMinutes by remember { mutableStateOf(25) }
    var selectedMode by remember { mutableStateOf(FocusMode.DEEP_FOCUS) }
    var showAbandonDialog by remember { mutableStateOf(false) }
    var showCustomDialog by remember { mutableStateOf(false) }
    var showStylePicker by remember { mutableStateOf(false) }

    val remainingMinutes = if (sessionState.state == FocusState.IDLE || sessionState.state == FocusState.FOCUS_COMPLETED || sessionState.state == FocusState.FOCUS_ABANDONED) {
        selectedDurationMinutes
    } else {
        sessionState.remainingSeconds / 60
    }
    val remainingSecs = if (sessionState.state == FocusState.IDLE || sessionState.state == FocusState.FOCUS_COMPLETED || sessionState.state == FocusState.FOCUS_ABANDONED) {
        0
    } else {
        sessionState.remainingSeconds % 60
    }
    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", remainingMinutes, remainingSecs)

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = when (sessionState.state) {
                        FocusState.FOCUS_ACTIVE, FocusState.RESUMED -> sessionState.mode.name.replace("_", " ")
                        FocusState.PAUSED -> "SESSION PAUSED"
                        FocusState.FOCUS_COMPLETED -> "SESSION COMPLETED"
                        FocusState.FOCUS_ABANDONED -> "SESSION ENDED"
                        else -> "READY TO FOCUS"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (sessionState.state == FocusState.IDLE) "Select a duration and start protection." else "Protection Active",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            // Radial Timer Centerpiece
            CalmTimerDisplay(
                remainingTimeText = formattedTime,
                progressFraction = if (sessionState.state == FocusState.IDLE || sessionState.state == FocusState.FOCUS_COMPLETED || sessionState.state == FocusState.FOCUS_ABANDONED) 1.0f else sessionState.progressFraction,
                statusLabel = when (sessionState.state) {
                    FocusState.FOCUS_ACTIVE, FocusState.RESUMED -> "Focusing"
                    FocusState.PAUSED -> "Paused"
                    else -> "$selectedDurationMinutes min"
                }
            )

            // Duration Pickers & Focus Style Switcher when IDLE
            if (sessionState.state == FocusState.IDLE || sessionState.state == FocusState.FOCUS_COMPLETED || sessionState.state == FocusState.FOCUS_ABANDONED) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Focus Style Switcher Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Focus Style: ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        CalmChip(
                            text = selectedMode.name.replace("_", " "),
                            isSelected = true,
                            onClick = { showStylePicker = true }
                        )
                    }

                    // Preset Chips + Custom Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(15, 25, 45, 60).forEach { mins ->
                            CalmChip(
                                text = "$mins m",
                                isSelected = selectedDurationMinutes == mins,
                                onClick = { selectedDurationMinutes = mins }
                            )
                        }
                        CalmChip(
                            text = "+ Custom",
                            isSelected = selectedDurationMinutes !in listOf(15, 25, 45, 60),
                            onClick = { showCustomDialog = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Selected Duration: $selectedDurationMinutes mins (${selectedDurationMinutes / 60}h ${selectedDurationMinutes % 60}m)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                when (sessionState.state) {
                    FocusState.IDLE, FocusState.FOCUS_COMPLETED, FocusState.FOCUS_ABANDONED -> {
                        CalmButton(
                            text = "Start Focus",
                            onClick = { viewModel.startSession(selectedDurationMinutes, selectedMode) },
                            variant = CalmButtonVariant.PRIMARY,
                            modifier = Modifier.fillMaxWidth(0.8f)
                        )
                    }
                    FocusState.FOCUS_ACTIVE, FocusState.RESUMED -> {
                        CalmButton(
                            text = "Pause",
                            onClick = { viewModel.pauseSession() },
                            variant = CalmButtonVariant.SECONDARY,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        CalmButton(
                            text = "End Session",
                            onClick = { showAbandonDialog = true },
                            variant = CalmButtonVariant.DANGER,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    FocusState.PAUSED -> {
                        CalmButton(
                            text = "Resume",
                            onClick = { viewModel.resumeSession() },
                            variant = CalmButtonVariant.PRIMARY,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        CalmButton(
                            text = "End Session",
                            onClick = { showAbandonDialog = true },
                            variant = CalmButtonVariant.DANGER,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    else -> {}
                }
            }
        }
    }

    // 1. Abandon Session Confirmation Dialog
    if (showAbandonDialog) {
        CalmDialog(
            title = "End Focus Session?",
            message = "Ending your session early will be recorded locally in your statistics. Are you sure you want to abandon focus?",
            confirmText = "End Session",
            dismissText = "Keep Focusing",
            isDanger = true,
            onConfirm = {
                showAbandonDialog = false
                viewModel.abandonSession()
            },
            onDismiss = { showAbandonDialog = false }
        )
    }

    // 2. Interactive Custom Duration Dialog
    if (showCustomDialog) {
        var tempMinutes by remember { mutableStateOf(selectedDurationMinutes) }

        CalmDialog(
            title = "Custom Focus Duration",
            message = "Select or adjust your target focus duration:",
            confirmText = "Set ${tempMinutes}m",
            dismissText = "Cancel",
            onConfirm = {
                selectedDurationMinutes = tempMinutes
                showCustomDialog = false
            },
            onDismiss = { showCustomDialog = false }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Quick Hour/Minute Preset Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(30, 90, 120, 180).forEach { mins ->
                        val label = if (mins >= 60) "${mins / 60}h" else "${mins}m"
                        CalmChip(
                            text = label,
                            isSelected = tempMinutes == mins,
                            onClick = { tempMinutes = mins }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stepper Controls (-15m, -5m, +5m, +15m)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CalmButton(
                        text = "-15m",
                        onClick = { tempMinutes = maxOf(5, tempMinutes - 15) },
                        variant = CalmButtonVariant.SECONDARY
                    )
                    CalmButton(
                        text = "-5m",
                        onClick = { tempMinutes = maxOf(5, tempMinutes - 5) },
                        variant = CalmButtonVariant.SECONDARY
                    )
                    Text(
                        text = "${tempMinutes}m",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    CalmButton(
                        text = "+5m",
                        onClick = { tempMinutes = minOf(300, tempMinutes + 5) },
                        variant = CalmButtonVariant.SECONDARY
                    )
                    CalmButton(
                        text = "+15m",
                        onClick = { tempMinutes = minOf(300, tempMinutes + 15) },
                        variant = CalmButtonVariant.SECONDARY
                    )
                }
            }
        }
    }

    // 3. Interactive Focus Style Switcher Dialog
    if (showStylePicker) {
        CalmDialog(
            title = "Select Focus Profile",
            message = "Choose the protection profile for your upcoming focus session:",
            confirmText = "Done",
            dismissText = null,
            onConfirm = { showStylePicker = false },
            onDismiss = { showStylePicker = false }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FocusMode.values().forEach { mode ->
                    CalmCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = mode.name.replace("_", " "),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = if (selectedMode == mode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            CalmChip(
                                text = if (selectedMode == mode) "Active ✓" else "Select",
                                isSelected = selectedMode == mode,
                                onClick = {
                                    selectedMode = mode
                                    showStylePicker = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
