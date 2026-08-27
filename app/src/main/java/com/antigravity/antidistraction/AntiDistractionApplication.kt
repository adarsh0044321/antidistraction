package com.antigravity.antidistraction

import android.app.Application
import com.antigravity.antidistraction.util.Logger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AntiDistractionApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Logger.i("AntiDistractionApplication", "Application initialized.")
    }
}
