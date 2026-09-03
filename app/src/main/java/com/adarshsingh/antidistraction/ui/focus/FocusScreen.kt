package com.adarshsingh.antidistraction.ui.focus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = false,
    onToggleDarkMode: (() -> Unit)? = null
) {
    val sessionState by viewModel.sessionState.collectAsState()

    var selectedDurationMinutes by remember { mutableStateOf(25) }
    var selectedMode by remember { mutableStateOf(FocusMode.DEEP_FOCUS) }

    // Multi-Step Confirmation Dialog States
    var showStep1Confirmation by remember { mutableStateOf(false) }
    var showStep2Confirmation by remember { mutableStateOf(false) }
    var showStep3Confirmation by remember { mutableStateOf(false) }
    var verificationTextInput by remember { mutableStateOf("") }

    var showCustomDialog by remember { mutableStateOf(false) }
    var showStylePicker by remember { mutableStateOf(false) }

    val isChallengeActive = sessionState.mode == FocusMode.CHALLENGE || (sessionState.state != FocusState.IDLE && sessionState.targetDurationMs == 0L)

    val formattedTime = if (sessionState.state == FocusState.IDLE || sessionState.state == FocusState.FOCUS_COMPLETED || sessionState.state == FocusState.FOCUS_ABANDONED) {
        if (selectedMode == FocusMode.CHALLENGE) "∞ Open" else String.format(Locale.getDefault(), "%02d:00", selectedDurationMinutes)
    } else if (isChallengeActive) {
        val hours = sessionState.remainingSeconds / 3600
        val mins = (sessionState.remainingSeconds % 3600) / 60
        val secs = sessionState.remainingSeconds % 60
        if (hours > 0) String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, mins, secs) else String.format(Locale.getDefault(), "%02d:%02d", mins, secs)
    } else {
        val remainingMinutes = sessionState.remainingSeconds / 60
        val remainingSecs = sessionState.remainingSeconds % 60
        String.format(Locale.getDefault(), "%02d:%02d", remainingMinutes, remainingSecs)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            com.adarshsingh.antidistraction.ui.components.CalmTopBar(
                title = "Anti-Distraction",
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode
            )
        }
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
                        FocusState.FOCUS_ACTIVE, FocusState.RESUMED -> if (isChallengeActive) "CHALLENGE MODE ACTIVE 🏆" else sessionState.mode.name.replace("_", " ")
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
                progressFraction = if (sessionState.state == FocusState.IDLE || sessionState.state == FocusState.FOCUS_COMPLETED || sessionState.state == FocusState.FOCUS_ABANDONED || isChallengeActive) 1.0f else sessionState.progressFraction,
                statusLabel = when (sessionState.state) {
                    FocusState.FOCUS_ACTIVE, FocusState.RESUMED -> if (isChallengeActive) "Stopwatch Active" else "Focusing"
                    FocusState.PAUSED -> "Paused"
                    else -> if (selectedMode == FocusMode.CHALLENGE) "Challenge Mode" else "$selectedDurationMinutes min"
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

                    // Preset Chips + Custom Button + Challenge Chip
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(15, 25, 45, 60).forEach { mins ->
                            CalmChip(
                                text = "$mins m",
                                isSelected = selectedMode != FocusMode.CHALLENGE && selectedDurationMinutes == mins,
                                onClick = {
                                    if (selectedMode == FocusMode.CHALLENGE) selectedMode = FocusMode.DEEP_FOCUS
                                    selectedDurationMinutes = mins
                                }
                            )
                        }
                        CalmChip(
                            text = "🏆 Challenge",
                            isSelected = selectedMode == FocusMode.CHALLENGE,
                            onClick = {
                                selectedMode = FocusMode.CHALLENGE
                                selectedDurationMinutes = 0
                            }
                        )
                        CalmChip(
                            text = "+",
                            isSelected = selectedMode != FocusMode.CHALLENGE && selectedDurationMinutes !in listOf(15, 25, 45, 60),
                            onClick = { showCustomDialog = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (selectedMode == FocusMode.CHALLENGE) "Selected Mode: Challenge (Endless Stopwatch Focus)" else "Selected Duration: $selectedDurationMinutes mins (${selectedDurationMinutes / 60}h ${selectedDurationMinutes % 60}m)",
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
                            text = if (selectedMode == FocusMode.CHALLENGE) "Start Challenge Mode" else "Start Focus",
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
                            onClick = { showStep1Confirmation = true },
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
                            onClick = { showStep1Confirmation = true },
                            variant = CalmButtonVariant.DANGER,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    else -> {}
                }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // MULTI-STEP CONFIRMATION SEQUENCE (3 STEPS TO PREVENT Premature Ending)
    // ─────────────────────────────────────────────────────────────

    // STEP 1: Streak Protection Warning
    if (showStep1Confirmation) {
        CalmDialog(
            title = if (isChallengeActive) "🏆 Finish Challenge Focus? (Step 1 of 3)" else "🔥 Break Focus Streak? (Step 1 of 3)",
            message = if (isChallengeActive) {
                "You are currently running an open-ended Challenge Mode session. Are you ready to lock in your score and finish?"
            } else {
                "You are currently in an active ${sessionState.mode.name.replace("_", " ")} session. Stopping now will interrupt your focus momentum.\n\nAre you sure you want to proceed to session end?"
            },
            confirmText = "Proceed to Step 2 >",
            dismissText = "Keep Focusing 💪",
            isDanger = false,
            onConfirm = {
                showStep1Confirmation = false
                showStep2Confirmation = true
            },
            onDismiss = { showStep1Confirmation = false }
        )
    }

    // STEP 2: Focus Time Impact Warning
    if (showStep2Confirmation) {
        CalmDialog(
            title = if (isChallengeActive) "⏱️ Lock in Challenge Record (Step 2 of 3)" else "⏳ Confirm Early Session Exit (Step 2 of 3)",
            message = if (isChallengeActive) {
                "Current Focus Time: $formattedTime\n\nFinishing will record this challenge as COMPLETED in your history, update your focus stats, and unlock achievements! Ready to verify?"
            } else {
                "Current Focus Time: $formattedTime\n\nQuitting early will record an incomplete session in your statistics and lower your daily Focus Score. Are you really sure?"
            },
            confirmText = "Continue to Verification >",
            dismissText = "Stay in Focus",
            isDanger = !isChallengeActive,
            onConfirm = {
                showStep2Confirmation = false
                showStep3Confirmation = true
                verificationTextInput = ""
            },
            onDismiss = { showStep2Confirmation = false }
        )
    }

    // STEP 3: Final Verification Input ("END")
    if (showStep3Confirmation) {
        val isVerified = verificationTextInput.trim().equals("END", ignoreCase = false)

        CalmDialog(
            title = "🛡️ Final Verification Required (Step 3 of 3)",
            message = if (isChallengeActive) {
                "Type 'END' in capital letters below to confirm completing your Challenge Mode session:"
            } else {
                "To prevent impulse or accidental quitting, please type 'END' in capital letters below to confirm ending your focus session:"
            },
            confirmText = if (isVerified) (if (isChallengeActive) "Finish Challenge 🏆" else "Complete Session") else "Type 'END' to Enable",
            dismissText = "Cancel & Stay Focused",
            isDanger = !isChallengeActive,
            onConfirm = {
                if (isVerified) {
                    showStep3Confirmation = false
                    if (isChallengeActive) {
                        viewModel.completeSession()
                    } else {
                        viewModel.abandonSession()
                    }
                }
            },
            onDismiss = { showStep3Confirmation = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = verificationTextInput,
                    onValueChange = { verificationTextInput = it },
                    label = { Text("Type 'END'") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(0.9f)
                )
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // CUSTOM DURATION & STYLE PICKER DIALOGS
    // ─────────────────────────────────────────────────────────────

    // Interactive Custom Duration Dialog
    if (showCustomDialog) {
        var tempMinutes by remember { mutableStateOf(if (selectedDurationMinutes > 0) selectedDurationMinutes else 25) }

        CalmDialog(
            title = "Custom Focus Duration",
            message = "Select or adjust your target focus duration:",
            confirmText = "Set ${tempMinutes}m",
            dismissText = "Cancel",
            onConfirm = {
                selectedMode = FocusMode.DEEP_FOCUS
                selectedDurationMinutes = tempMinutes
                showCustomDialog = false
            },
            onDismiss = { showCustomDialog = false }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$tempMinutes min (${tempMinutes / 60}h ${tempMinutes % 60}m)",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(30 to "30m", 60 to "1h", 120 to "2h", 180 to "3h").forEach { (mins, label) ->
                        CalmChip(
                            text = label,
                            isSelected = tempMinutes == mins,
                            onClick = { tempMinutes = mins }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stepper Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val stepperList = listOf(
                        "-15m" to { tempMinutes = maxOf(5, tempMinutes - 15) },
                        "-5m" to { tempMinutes = maxOf(5, tempMinutes - 5) },
                        "+5m" to { tempMinutes = minOf(300, tempMinutes + 5) },
                        "+15m" to { tempMinutes = minOf(300, tempMinutes + 15) }
                    )

                    stepperList.forEach { (label, action) ->
                        OutlinedButton(
                            onClick = action,
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Clip
                            )
                        }
                    }
                }
            }
        }
    }

    // Interactive Focus Style Switcher Dialog
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
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = when (mode) {
                                        FocusMode.DEEP_FOCUS -> "Maximum strictness. Block all distracting apps."
                                        FocusMode.STUDY -> "High restriction with study tool allowances."
                                        FocusMode.WORK -> "Balanced work mode with warning alerts."
                                        FocusMode.LIGHT_FOCUS -> "Gentle reminders."
                                        FocusMode.CUSTOM -> "Custom rule parameters."
                                        FocusMode.CHALLENGE -> "Endless open-ended stopwatch focus."
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                            CalmChip(
                                text = if (selectedMode == mode) "Selected" else "Select",
                                isSelected = selectedMode == mode,
                                onClick = {
                                    selectedMode = mode
                                    if (mode == FocusMode.CHALLENGE) selectedDurationMinutes = 0
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
