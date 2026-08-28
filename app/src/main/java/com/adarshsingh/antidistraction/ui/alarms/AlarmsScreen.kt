package com.adarshsingh.antidistraction.ui.alarms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.adarshsingh.antidistraction.data.local.entity.WakeAlarmEntity
import com.adarshsingh.antidistraction.ui.components.CalmButton
import com.adarshsingh.antidistraction.ui.components.CalmButtonVariant
import com.adarshsingh.antidistraction.ui.components.CalmCard
import com.adarshsingh.antidistraction.ui.components.CalmChip
import com.adarshsingh.antidistraction.ui.components.CalmDialog
import com.adarshsingh.antidistraction.ui.components.CalmTopBar
import java.util.Locale

@Composable
fun AlarmsScreen(
    viewModel: AlarmsViewModel,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = false,
    onToggleDarkMode: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddAlarmDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CalmTopBar(
                title = "Wake Alarms & Sleep Protection",
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddAlarmDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Alarm")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card
            item {
                CalmCard {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Morning Focus Alarms",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Wake up aligned with your daily attention targets.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            CalmButton(
                                text = "+ Alarm",
                                onClick = { showAddAlarmDialog = true },
                                variant = CalmButtonVariant.PRIMARY
                            )
                        }
                    }
                }
            }

            // Alarms List
            item {
                Text(
                    text = "Configured Alarms",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            if (uiState.alarms.isEmpty()) {
                item {
                    CalmCard {
                        Text(
                            text = "No wake alarms set. Tap '+ Alarm' or the floating + button to configure your morning wake time and sleep protection!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(uiState.alarms, key = { it.id }) { alarm ->
                    AlarmCard(
                        alarm = alarm,
                        onToggle = { viewModel.toggleAlarm(alarm) }
                    )
                }
            }
        }
    }

    if (showAddAlarmDialog) {
        var selectedWakeHour by remember { mutableStateOf(7) }
        var selectedWakeMinute by remember { mutableStateOf(0) }
        var selectedBedtimeHour by remember { mutableStateOf(23) }

        val wakePresets = listOf(
            Pair(6, 0) to "6:00 AM",
            Pair(6, 30) to "6:30 AM",
            Pair(7, 0) to "7:00 AM",
            Pair(7, 30) to "7:30 AM",
            Pair(8, 0) to "8:00 AM"
        )

        val bedtimePresets = listOf(
            22 to "10:00 PM",
            23 to "11:00 PM",
            0 to "12:00 AM"
        )

        CalmDialog(
            title = "Set Morning Wake Alarm",
            message = "Configure wake-up time and planned bedtime for sleep protection:",
            confirmText = "Set Alarm",
            dismissText = "Cancel",
            onConfirm = {
                viewModel.saveAlarm(selectedWakeHour, selectedWakeMinute, selectedBedtimeHour)
                showAddAlarmDialog = false
            },
            onDismiss = { showAddAlarmDialog = false }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Wake Time Selection
                Text(
                    text = "Select Wake Time:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    wakePresets.forEach { (pair, label) ->
                        CalmChip(
                            text = label,
                            isSelected = selectedWakeHour == pair.first && selectedWakeMinute == pair.second,
                            onClick = {
                                selectedWakeHour = pair.first
                                selectedWakeMinute = pair.second
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Bedtime Selection
                Text(
                    text = "Planned Bedtime:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    bedtimePresets.forEach { (hour, label) ->
                        CalmChip(
                            text = label,
                            isSelected = selectedBedtimeHour == hour,
                            onClick = { selectedBedtimeHour = hour }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AlarmCard(
    alarm: WakeAlarmEntity,
    onToggle: () -> Unit
) {
    val formattedWakeTime = String.format(Locale.getDefault(), "%02d:%02d", alarm.timeHour, alarm.timeMinute)
    val formattedBedtime = String.format(Locale.getDefault(), "%02d:%02d", alarm.plannedBedtimeHour, alarm.plannedBedtimeMinute)

    // Calculate exact sleep duration in minutes (bedtime -> wake time)
    val wakeMinutesTotal = alarm.timeHour * 60 + alarm.timeMinute
    val bedtimeMinutesTotal = alarm.plannedBedtimeHour * 60 + alarm.plannedBedtimeMinute
    val diffMinutes = if (wakeMinutesTotal >= bedtimeMinutesTotal) {
        wakeMinutesTotal - bedtimeMinutesTotal
    } else {
        (24 * 60 - bedtimeMinutesTotal) + wakeMinutesTotal
    }

    val sleepHours = diffMinutes / 60
    val sleepMins = diffMinutes % 60
    val formattedSleepDuration = String.format(Locale.getDefault(), "%dh %02dm", sleepHours, sleepMins)
    val isSleepWarning = sleepHours < alarm.minimumSleepDurationHours

    CalmCard {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = formattedWakeTime,
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${alarm.title} • Bedtime: $formattedBedtime",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = alarm.isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sleep Duration Protection Card
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Planned Sleep: $formattedSleepDuration",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSleepWarning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                )
                if (isSleepWarning) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "(Below ${alarm.minimumSleepDurationHours}h minimum)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
