package com.antigravity.antidistraction.ui

import com.antigravity.antidistraction.ui.theme.DarkBackground
import com.antigravity.antidistraction.ui.theme.LightBackground
import org.junit.Assert.assertNotNull
import org.junit.Test

class VisualPolishTest {

    @Test
    fun themeColors_calmPaletteTokensAreDefined() {
        assertNotNull(DarkBackground)
        assertNotNull(LightBackground)
    }
}
