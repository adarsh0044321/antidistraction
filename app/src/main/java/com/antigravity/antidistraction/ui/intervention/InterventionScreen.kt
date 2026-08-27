package com.antigravity.antidistraction.ui.intervention

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.antigravity.antidistraction.domain.model.IntentionType
import com.antigravity.antidistraction.ui.components.CalmButton
import com.antigravity.antidistraction.ui.components.CalmButtonVariant
import com.antigravity.antidistraction.ui.components.CalmCard
import com.antigravity.antidistraction.ui.components.CalmChip
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InterventionScreen(
    targetPackageName: String,
    onReturnToFocus: (IntentionType?) -> Unit,
    onBypassGranted: (IntentionType?) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedIntention by remember { mutableStateOf<IntentionType?>(null) }
    var countdownSeconds by remember { mutableIntStateOf(10) }
    var isCountdownComplete by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (countdownSeconds > 0) {
            delay(1000L)
            countdownSeconds--
        }
        isCountdownComplete = true
    }

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
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Conscious Friction",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$targetPackageName is restricted during your focus session.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            // Intention Question Card
            CalmCard {
                Text(
                    text = "What do you need access for?",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IntentionType.values().forEach { intention ->
                        val isSelected = selectedIntention == intention
                        CalmChip(
                            text = intention.displayName,
                            isSelected = isSelected,
                            onClick = { selectedIntention = intention }
                        )
                    }
                }
            }

            // Delay Banner
            if (!isCountdownComplete) {
                Text(
                    text = "Take $countdownSeconds seconds before deciding...",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Actions
            Column(modifier = Modifier.fillMaxWidth()) {
                CalmButton(
                    text = "Return to Focus",
                    onClick = { onReturnToFocus(selectedIntention) },
                    variant = CalmButtonVariant.PRIMARY,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    CalmButton(
                        text = "Request 2m Bypass",
                        onClick = { onBypassGranted(selectedIntention) },
                        variant = CalmButtonVariant.SECONDARY,
                        enabled = isCountdownComplete && selectedIntention != null,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
