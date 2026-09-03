package com.adarshsingh.antidistraction.ui.analytics

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adarshsingh.antidistraction.domain.badge.FocusBadge
import com.adarshsingh.antidistraction.ui.components.CalmButton
import com.adarshsingh.antidistraction.ui.components.CalmButtonVariant
import com.adarshsingh.antidistraction.ui.components.CalmCard
import com.adarshsingh.antidistraction.ui.components.CalmDialog
import com.adarshsingh.antidistraction.ui.components.CalmProgressIndicator
import com.adarshsingh.antidistraction.ui.components.CalmTopBar

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    onToggleDarkMode: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    var showScoreFormulaDialog by remember { mutableStateOf(false) }
    var showAchievementsModal by remember { mutableStateOf(false) }
    var selectedBadgeDetail by remember { mutableStateOf<FocusBadge?>(null) }
    var selectedAchievementTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CalmTopBar(
                title = "Analytics & Insights",
                isDarkMode = isDarkMode,
                onBackClick = onBackClick,
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
            // Focus Score Card with (?) Info Button
            item {
                CalmCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Focus Score",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                                onClick = { showScoreFormulaDialog = true },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Score Formula Info",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Text(
                            text = "${uiState.scoreDetails.totalScore}/100",
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Grade: ${uiState.scoreDetails.scoreGrade}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    CalmProgressIndicator(progressFraction = uiState.scoreDetails.totalScore.toFloat() / 100f)
                }
            }

            // Achievements Button Trigger Card
            item {
                CalmCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Achievements",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Achievements & Badges",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${uiState.badges.count { it.isUnlocked }}/${uiState.badges.size} Badges Unlocked",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        CalmButton(
                            text = "View",
                            onClick = { showAchievementsModal = true },
                            variant = CalmButtonVariant.PRIMARY
                        )
                    }
                }
            }

            // Summary Metrics Grid
            item {
                CalmCard {
                    Text(
                        text = "Weekly Focus Statistics",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Total Focus", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
                            Text(text = "${uiState.summary.totalFocusTimeMinutes} mins", style = MaterialTheme.typography.titleLarge)
                        }
                        Column {
                            Text(text = "Sessions", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
                            Text(text = "${uiState.summary.completedSessionsCount} done", style = MaterialTheme.typography.titleLarge)
                        }
                        Column {
                            Text(text = "Resisted", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
                            Text(text = "${uiState.summary.resistedAttemptsCount} apps", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }

            // Behavioral Insights
            item {
                Text(
                    text = "Behavioral Insights",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(uiState.insights, key = { it.title }) { insight ->
                CalmCard {
                    Text(
                        text = insight.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = insight.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Focus Session History
            item {
                Text(
                    text = "Focus Session History",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            if (uiState.recentSessions.isEmpty()) {
                item {
                    CalmCard {
                        Text(
                            text = "No focus sessions recorded yet. Start a session to track your progress!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(uiState.recentSessions, key = { it.id }) { session ->
                    CalmCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                val modeLabel = if (session.focusMode.name == "CHALLENGE") "🏆 Challenge Mode" else session.focusMode.name.replace("_", " ")
                                val durationMins = if (session.targetDurationMs > 0L) {
                                    session.targetDurationMs / (60 * 1000)
                                } else {
                                    maxOf(1L, ((session.actualEndTimeMs ?: session.startTimeMs) - session.startTimeMs) / (60 * 1000))
                                }

                                Text(
                                    text = modeLabel,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$durationMins mins • Interventions: ${session.totalInterventions}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = session.state.name.replace("FOCUS_", ""),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (session.state.name.contains("COMPLETED")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }
        }
    }

    // 1. Focus Score Calculation Formula Info Modal (?)
    if (showScoreFormulaDialog) {
        CalmDialog(
            title = "How Productivity Score is Calculated",
            message = "Your Daily Focus Score (0 - 100 pts) is computed dynamically based on four key focus metrics:\n\n" +
                    "• ⏱️ Focus Time Target (40 pts): Points awarded for total minutes of deep focus completed.\n\n" +
                    "• 🎯 Goal Execution (30 pts): Ratio of daily productivity goals completed.\n\n" +
                    "• 🛡️ Distraction Resistance (20 pts): Intercepted app attempt resistance without bypassing.\n\n" +
                    "• 🔥 Consistency (10 pts): Multi-day focus streak bonus.",
            confirmText = "Got it",
            dismissText = null,
            onConfirm = { showScoreFormulaDialog = false },
            onDismiss = { showScoreFormulaDialog = false }
        )
    }

    // 2. Dedicated Achievements Modal Viewer
    if (showAchievementsModal) {
        val unlockedBadges = uiState.badges.filter { it.isUnlocked }
        val lockedBadges = uiState.badges.filter { !it.isUnlocked }

        CalmDialog(
            title = "Achievements & Badges",
            message = "",
            confirmText = "Close",
            dismissText = null,
            onConfirm = { showAchievementsModal = false },
            onDismiss = { showAchievementsModal = false }
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                TabRow(
                    selectedTabIndex = selectedAchievementTabIndex,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Tab(
                        selected = selectedAchievementTabIndex == 0,
                        onClick = { selectedAchievementTabIndex = 0 },
                        text = { Text("Unlocked (${unlockedBadges.size})", style = MaterialTheme.typography.labelSmall) }
                    )
                    Tab(
                        selected = selectedAchievementTabIndex == 1,
                        onClick = { selectedAchievementTabIndex = 1 },
                        text = { Text("Yet to Unlock (${lockedBadges.size})", style = MaterialTheme.typography.labelSmall) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val displayList = if (selectedAchievementTabIndex == 0) unlockedBadges else lockedBadges

                if (displayList.isEmpty()) {
                    Text(
                        text = if (selectedAchievementTabIndex == 0) "No achievements unlocked yet. Keep focusing!" else "All achievements unlocked! Amazing work!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 420.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(displayList, key = { it.id }) { badge ->
                            CalmCard(
                                modifier = Modifier.clickable { selectedBadgeDetail = badge }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = badge.iconEmoji,
                                        style = MaterialTheme.typography.displayLarge
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = badge.title,
                                            style = MaterialTheme.typography.titleLarge,
                                            color = if (badge.isUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = badge.description,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = badge.progressText,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (badge.isUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 3. Interactive Badge Detail Modal with (X) Close Icon
    if (selectedBadgeDetail != null) {
        val badge = selectedBadgeDetail!!
        CalmDialog(
            title = "${badge.iconEmoji} ${badge.title}",
            message = badge.description + "\n\n" +
                    "Status: ${if (badge.isUnlocked) "Unlocked 🏆" else "Locked 🔒"}\n" +
                    "Progress: ${badge.progressText}",
            confirmText = "Close",
            dismissText = null,
            onConfirm = { selectedBadgeDetail = null },
            onDismiss = { selectedBadgeDetail = null }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { selectedBadgeDetail = null }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Badge Detail",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
