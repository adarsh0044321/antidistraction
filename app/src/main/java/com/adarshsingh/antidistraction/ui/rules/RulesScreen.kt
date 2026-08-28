package com.adarshsingh.antidistraction.ui.rules

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
import com.adarshsingh.antidistraction.ui.components.CalmButton
import com.adarshsingh.antidistraction.ui.components.CalmButtonVariant
import com.adarshsingh.antidistraction.ui.components.CalmCard
import com.adarshsingh.antidistraction.ui.components.CalmEmptyState
import com.adarshsingh.antidistraction.ui.components.CalmTopBar

private fun formatAppLabel(packageName: String): String {
    return when {
        packageName.contains("instagram", ignoreCase = true) -> "Instagram"
        packageName.contains("whatsapp", ignoreCase = true) -> "WhatsApp"
        packageName.contains("youtube", ignoreCase = true) -> "YouTube"
        packageName.contains("facebook", ignoreCase = true) -> "Facebook"
        packageName.contains("twitter", ignoreCase = true) -> "Twitter / X"
        packageName.contains("tiktok", ignoreCase = true) -> "TikTok"
        packageName.contains("snapchat", ignoreCase = true) -> "Snapchat"
        packageName.contains("reddit", ignoreCase = true) -> "Reddit"
        else -> packageName.substringAfterLast('.').replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}

@Composable
fun RulesScreen(
    viewModel: RulesViewModel,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    onToggleDarkMode: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CalmTopBar(
                title = "Restriction Rules & Active Exceptions",
                isDarkMode = isDarkMode,
                onBackClick = onBackClick,
                onToggleDarkMode = onToggleDarkMode
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            Text(
                text = "Active Temporary Exceptions",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.activeExceptions.isEmpty()) {
                CalmEmptyState(
                    title = "No Active Temporary Exceptions",
                    description = "Temporary exceptions requested during bypass checks will appear here with live countdown expiration timers."
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.activeExceptions, key = { it.packageName }) { exception ->
                        val diffMs = (exception.expirationTimestampMs - System.currentTimeMillis()).coerceAtLeast(0L)
                        val mins = diffMs / 60000L
                        val secs = (diffMs % 60000L) / 1000L
                        val formattedRemaining = if (mins > 0) "${mins}m ${secs}s remaining" else "${secs}s remaining"
                        val appTitle = formatAppLabel(exception.packageName)

                        CalmCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = appTitle,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = exception.packageName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Reason: ${exception.grantedReason} • $formattedRemaining",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                CalmButton(
                                    text = "Revoke",
                                    onClick = { viewModel.revokeException(exception.packageName) },
                                    variant = CalmButtonVariant.DANGER
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
