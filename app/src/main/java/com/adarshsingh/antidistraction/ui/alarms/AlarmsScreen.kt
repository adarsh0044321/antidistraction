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
import com.adarshsingh.antidistraction.ui.components.CalmDialog
import com.adarshsingh.antidistraction.ui.components.CalmTopBar
import java.util.Locale

@Composable
fun AlarmsScreen(
    viewModel: AlarmsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddAlarmDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CalmTopBar(title = "Wake Alarms & Sleep Protection")
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
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
                        CalmButton(
                            text = "+ Alarm",
                            onClick = { showAddAlarmDialog = true },
                            variant = CalmButtonVariant.PRIMARY
                        )
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
                            text = "No wake alarms set. Tap '+ Alarm' above to configure your morning wake time and sleep protection!",
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
        var wakeHourStr by remember { mutableStateOf("7") }
        var wakeMinuteStr by remember { mutableStateOf("0") }
        var bedtimeHourStr by remember { mutableStateOf("23") }

        CalmDialog(
            title = "Set Wake Alarm",
            message = "Enter wake time (0-23 Hour, 0-59 Minute) and planned bedtime hour (e.g. 23 for 11 PM):",
            confirmText = "Set Alarm",
            dismissText = "Cancel",
            onConfirm = {
                val hour = wakeHourStr.toIntOrNull() ?: 7
                val minute = wakeMinuteStr.toIntOrNull() ?: 0
                val bedtime = bedtimeHourStr.toIntOrNull() ?: 23

                if (hour in 0..23 && minute in 0..59) {
                    viewModel.saveAlarm(hour, minute, bedtime)
                }
                showAddAlarmDialog = false
            },
            onDismiss = { showAddAlarmDialog = false }
        )
    }
}

@Composable
private fun AlarmCard(
    alarm: WakeAlarmEntity,
    onToggle: () -> Unit
) {
    val formattedWakeTime = String.format(Locale.getDefault(), "%02d:%02d", alarm.timeHour, alarm.timeMinute)
    val formattedBedtime = String.format(Locale.getDefault(), "%02d:00", alarm.plannedBedtimeHour)

    // Calculate sleep duration (bedtime -> wake time)
    val sleepDurationHours = if (alarm.timeHour >= alarm.plannedBedtimeHour) {
        alarm.timeHour - alarm.plannedBedtimeHour
    } else {
        (24 - alarm.plannedBedtimeHour) + alarm.timeHour
    }

    val isSleepWarning = sleepDurationHours < alarm.minimumSleepDurationHours

    CalmCard {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
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
                    text = "Planned Sleep: ${sleepDurationHours}h 00m",
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
