package com.adarshsingh.antidistraction.security

import org.junit.Assert.assertFalse
import org.junit.Test
import java.io.File

class PrivacyHardeningTest {

    @Test
    fun appManifest_doesNotContainInternetPermission() {
        val manifestFile = File("src/main/AndroidManifest.xml")
        if (manifestFile.exists()) {
            val content = manifestFile.readText()
            assertFalse("Manifest must not request INTERNET permission", content.contains("android.permission.INTERNET"))
        }
    }
}
