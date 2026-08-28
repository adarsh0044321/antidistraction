package com.adarshsingh.antidistraction.ui.apps

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adarshsingh.antidistraction.domain.model.AppInfo
import com.adarshsingh.antidistraction.ui.components.CalmButton
import com.adarshsingh.antidistraction.ui.components.CalmButtonVariant
import com.adarshsingh.antidistraction.ui.components.CalmCard
import com.adarshsingh.antidistraction.ui.components.CalmChip
import com.adarshsingh.antidistraction.ui.components.CalmEmptyState
import com.adarshsingh.antidistraction.ui.components.CalmTopBar

@Composable
fun AppManagementScreen(
    viewModel: AppManagementViewModel,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CalmTopBar(
                title = "Protected Applications",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search installed apps...") },
                shape = MaterialTheme.shapes.medium,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.categories) { category ->
                    CalmChip(
                        text = category,
                        isSelected = uiState.selectedCategoryFilter == category,
                        onClick = { viewModel.setCategoryFilter(category) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Content Area
            if (uiState.isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Discovering installed applications...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            } else if (uiState.filteredApps.isEmpty()) {
                CalmEmptyState(
                    title = "No applications found",
                    description = "Try adjusting your search query or category filter."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.filteredApps, key = { it.packageName }) { app ->
                        AppRowCard(
                            app = app,
                            onToggleBlock = { viewModel.toggleAppBlocked(app) },
                            onToggleEmergency = { viewModel.toggleEmergencyApp(app) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppRowCard(
    app: AppInfo,
    onToggleBlock: () -> Unit,
    onToggleEmergency: () -> Unit
) {
    CalmCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.appName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${app.category} • ${app.packageName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CalmButton(
                        text = if (app.isEmergencyApp) "Emergency" else "Normal",
                        onClick = onToggleEmergency,
                        variant = if (app.isEmergencyApp) CalmButtonVariant.SECONDARY else CalmButtonVariant.TEXT
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = if (app.isBlocked) "Restrict" else "Allow",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (app.isBlocked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = app.isBlocked,
                        onCheckedChange = { onToggleBlock() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onError,
                            checkedTrackColor = MaterialTheme.colorScheme.error
                        )
                    )
                }
            }
        }
    }
}
