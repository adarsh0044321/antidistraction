package com.antigravity.antidistraction.ui.onboarding

import com.antigravity.antidistraction.domain.model.FocusMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingViewModelTest {

    @Test
    fun uiState_defaultValuesAreCorrect() {
        val state = OnboardingUiState()
        assertEquals(1, state.currentStep)
        assertEquals(4, state.totalSteps)
        assertEquals(FocusMode.DEEP_FOCUS, state.selectedFocusMode)
        assertFalse(state.isOnboardingComplete)
    }

    @Test
    fun toggleDistraction_addsAndRemovesCategory() {
        var state = OnboardingUiState(selectedDistractions = setOf("Games"))
        val category = "Social Media"
        
        // Add
        var current = state.selectedDistractions.toMutableSet()
        current.add(category)
        state = state.copy(selectedDistractions = current)
        assertTrue(state.selectedDistractions.contains("Social Media"))

        // Remove
        current = state.selectedDistractions.toMutableSet()
        current.remove(category)
        state = state.copy(selectedDistractions = current)
        assertFalse(state.selectedDistractions.contains("Social Media"))
    }
}
