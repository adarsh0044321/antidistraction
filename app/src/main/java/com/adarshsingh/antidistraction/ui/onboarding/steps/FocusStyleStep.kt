package com.adarshsingh.antidistraction.ui.onboarding.steps

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adarshsingh.antidistraction.domain.model.FocusMode
import com.adarshsingh.antidistraction.ui.components.CalmCard

data class FocusStyleOption(
    val mode: FocusMode,
    val title: String,
    val description: String
)

val FOCUS_STYLES = listOf(
    FocusStyleOption(
        mode = FocusMode.DEEP_FOCUS,
        title = "Deep Focus",
        description = "Maximum restriction. Notification suppression and strong friction for important work & study."
    ),
    FocusStyleOption(
        mode = FocusMode.STUDY,
        title = "Study Mode",
        description = "Restrictive but practical. Allows calculator, browser research, educational notes, and essential tools."
    ),
    FocusStyleOption(
        mode = FocusMode.WORK,
        title = "Work Productivity",
        description = "Focused productivity while maintaining necessary workplace messaging and communication contacts."
    ),
    FocusStyleOption(
        mode = FocusMode.LIGHT_FOCUS,
        title = "Light Focus",
        description = "Gentle warnings and intention prompts instead of hard blocking."
    )
)

@Composable
fun FocusStyleStep(
    selectedMode: FocusMode,
    onSelectMode: (FocusMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Choose your focus style",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Select your default protection profile. You can switch modes or customize rules anytime.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.tertiary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FOCUS_STYLES.forEach { style ->
                val isSelected = style.mode == selectedMode
                CalmCard(
                    modifier = Modifier.clickable { onSelectMode(style.mode) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onSelectMode(style.mode) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = style.title,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = style.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
