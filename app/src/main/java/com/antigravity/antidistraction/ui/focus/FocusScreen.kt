package com.antigravity.antidistraction.ui.focus

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
import com.antigravity.antidistraction.domain.model.FocusMode
import com.antigravity.antidistraction.domain.model.FocusState
import com.antigravity.antidistraction.ui.components.CalmButton
import com.antigravity.antidistraction.ui.components.CalmButtonVariant
import com.antigravity.antidistraction.ui.components.CalmChip
import com.antigravity.antidistraction.ui.components.CalmDialog
import com.antigravity.antidistraction.ui.components.CalmTimerDisplay
import java.util.Locale

@Composable
fun FocusScreen(
    viewModel: FocusViewModel,
    modifier: Modifier = Modifier
) {
    val sessionState by viewModel.sessionState.collectAsState()
    var selectedDurationMinutes by remember { mutableStateOf(25) }
    var showAbandonDialog by remember { mutableStateOf(false) }

    val remainingMinutes = sessionState.remainingSeconds / 60
    val remainingSecs = sessionState.remainingSeconds % 60
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
                progressFraction = sessionState.progressFraction,
                statusLabel = when (sessionState.state) {
                    FocusState.FOCUS_ACTIVE, FocusState.RESUMED -> "Focusing"
                    FocusState.PAUSED -> "Paused"
                    else -> "$selectedDurationMinutes min"
                }
            )

            // Duration Pickers when IDLE
            if (sessionState.state == FocusState.IDLE || sessionState.state == FocusState.FOCUS_COMPLETED || sessionState.state == FocusState.FOCUS_ABANDONED) {
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
                            onClick = { viewModel.startSession(selectedDurationMinutes, FocusMode.DEEP_FOCUS) },
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
}
