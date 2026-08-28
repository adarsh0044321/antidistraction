package com.adarshsingh.antidistraction.ui.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adarshsingh.antidistraction.ui.components.CalmButton
import com.adarshsingh.antidistraction.ui.components.CalmButtonVariant
import com.adarshsingh.antidistraction.ui.components.CalmProgressIndicator
import com.adarshsingh.antidistraction.ui.onboarding.steps.DistractionSelectionStep
import com.adarshsingh.antidistraction.ui.onboarding.steps.FocusStyleStep
import com.adarshsingh.antidistraction.ui.onboarding.steps.OnboardingCompleteStep
import com.adarshsingh.antidistraction.ui.onboarding.steps.PermissionSetupStep

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onFinishOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isOnboardingComplete) {
        onFinishOnboarding()
    }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            // Stepper Progress
            CalmProgressIndicator(
                progressFraction = uiState.currentStep.toFloat() / uiState.totalSteps.toFloat()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Step Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                when (uiState.currentStep) {
                    1 -> DistractionSelectionStep(
                        selectedCategories = uiState.selectedDistractions,
                        onToggleCategory = { viewModel.toggleDistraction(it) }
                    )
                    2 -> FocusStyleStep(
                        selectedMode = uiState.selectedFocusMode,
                        onSelectMode = { viewModel.setFocusMode(it) }
                    )
                    3 -> PermissionSetupStep(
                        isUsageAccessGranted = uiState.isUsageAccessGranted,
                        isAccessibilityGranted = uiState.isAccessibilityGranted,
                        isOverlayGranted = uiState.isOverlayGranted
                    )
                    4 -> OnboardingCompleteStep(
                        selectedMode = uiState.selectedFocusMode,
                        protectedAppsCount = uiState.selectedDistractions.size
                    )
                }
            }

            // Navigation Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (uiState.currentStep > 1) {
                    CalmButton(
                        text = "Back",
                        onClick = { viewModel.previousStep() },
                        variant = CalmButtonVariant.TEXT,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }

                CalmButton(
                    text = if (uiState.currentStep == uiState.totalSteps) "Start Focusing" else "Continue",
                    onClick = { viewModel.nextStep() },
                    variant = CalmButtonVariant.PRIMARY,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
