package com.adarshsingh.antidistraction

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.adarshsingh.antidistraction.ui.main.MainAppShell
import com.adarshsingh.antidistraction.ui.main.MainViewModel
import com.adarshsingh.antidistraction.ui.onboarding.OnboardingScreen
import com.adarshsingh.antidistraction.ui.onboarding.OnboardingViewModel
import com.adarshsingh.antidistraction.ui.theme.AntiDistractionTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AntiDistractionTheme {
                val isFirstLaunch by mainViewModel.isFirstLaunch.collectAsState()

                if (isFirstLaunch) {
                    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
                    OnboardingScreen(
                        viewModel = onboardingViewModel,
                        onFinishOnboarding = {
                            // Onboarding completed, state updates asynchronously to navigate to MainAppShell
                        }
                    )
                } else {
                    MainAppShell()
                }
            }
        }
    }

    companion object {
        fun start(context: android.content.Context) {
            val intent = android.content.Intent(context, MainActivity::class.java).apply {
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            context.startActivity(intent)
        }
    }
}
