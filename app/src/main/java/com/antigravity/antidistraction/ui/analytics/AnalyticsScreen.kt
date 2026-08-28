package com.antigravity.antidistraction.ui.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.antigravity.antidistraction.ui.components.CalmCard
import com.antigravity.antidistraction.ui.components.CalmProgressIndicator
import com.antigravity.antidistraction.ui.components.CalmTopBar

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CalmTopBar(
                title = "Analytics & Insights",
                onBackClick = onBackClick
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
            // Focus Score Card
            item {
                CalmCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Focus Score",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Grade: ${uiState.scoreDetails.scoreGrade}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = "${uiState.scoreDetails.totalScore}/100",
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    CalmProgressIndicator(progressFraction = uiState.scoreDetails.totalScore.toFloat() / 100f)
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
                    text = "Focus Badges & Achievements",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(uiState.badges, key = { it.id }) { badge ->
                CalmCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = badge.iconEmoji,
                            style = MaterialTheme.typography.displayLarge
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = badge.title,
                                style = MaterialTheme.typography.titleLarge,
                                color = if (badge.isUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = badge.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = badge.progressText,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (badge.isUnlocked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary
                            )
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
                                Text(
                                    text = session.focusMode.name.replace("_", " "),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${session.targetDurationMs / (60 * 1000)} mins • Interventions: ${session.totalInterventions}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = session.state.name.replace("FOCUS_", ""),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (session.state.name.contains("COMPLETED")) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }
        }
    }
}
