package com.adarshsingh.antidistraction.ui

import com.adarshsingh.antidistraction.ui.theme.DarkBackground
import com.adarshsingh.antidistraction.ui.theme.LightBackground
import org.junit.Assert.assertNotNull
import org.junit.Test

class VisualPolishTest {

    @Test
    fun themeColors_calmPaletteTokensAreDefined() {
        assertNotNull(DarkBackground)
        assertNotNull(LightBackground)
    }
}
