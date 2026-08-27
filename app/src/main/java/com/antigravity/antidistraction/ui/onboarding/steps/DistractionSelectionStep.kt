package com.antigravity.antidistraction.ui.onboarding.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.antigravity.antidistraction.ui.components.CalmChip

val DISTRACTION_CATEGORIES = listOf(
    "Social Media",
    "Short Videos",
    "News & Feeds",
    "Mobile Games",
    "Shopping",
    "Messaging",
    "Streaming Video",
    "Web Browsing"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DistractionSelectionStep(
    selectedCategories: Set<String>,
    onToggleCategory: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "What distracts you most?",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Select the types of applications that interrupt your focus. We'll help you introduce conscious friction.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.tertiary
        )

        Spacer(modifier = Modifier.height(32.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DISTRACTION_CATEGORIES.forEach { category ->
                val isSelected = selectedCategories.contains(category)
                CalmChip(
                    text = category,
                    isSelected = isSelected,
                    onClick = { onToggleCategory(category) }
                )
            }
        }
    }
}
