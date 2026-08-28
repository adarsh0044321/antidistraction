package com.adarshsingh.antidistraction.ui

import com.adarshsingh.antidistraction.ui.components.CalmButtonVariant
import org.junit.Assert.assertEquals
import org.junit.Test

class CalmComponentTest {

    @Test
    fun calmButtonVariant_allVariantsExist() {
        val variants = CalmButtonVariant.values()
        assertEquals(4, variants.size)
        assertEquals(CalmButtonVariant.PRIMARY, CalmButtonVariant.valueOf("PRIMARY"))
        assertEquals(CalmButtonVariant.DANGER, CalmButtonVariant.valueOf("DANGER"))
    }
}
