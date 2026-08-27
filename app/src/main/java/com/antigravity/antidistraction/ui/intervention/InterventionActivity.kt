package com.antigravity.antidistraction.ui.intervention

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import com.antigravity.antidistraction.MainActivity
import com.antigravity.antidistraction.ui.theme.AntiDistractionTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InterventionActivity : ComponentActivity() {

    companion object {
        private const val EXTRA_PACKAGE_NAME = "extra_package_name"

        fun start(context: Context, packageName: String) {
            val intent = Intent(context, InterventionActivity::class.java).apply {
                putExtra(EXTRA_PACKAGE_NAME, packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: "Restricted App"

        setContent {
            AntiDistractionTheme {
                val viewModel: InterventionViewModel = hiltViewModel()
                InterventionScreen(
                    targetPackageName = packageName,
                    onReturnToFocus = { intention ->
                        viewModel.recordReturnedToFocus(packageName, intention)
                        MainActivity.start(this@InterventionActivity)
                        finish()
                    },
                    onBypassGranted = { intention ->
                        viewModel.recordBypassGranted(packageName, intention, durationMinutes = 2)

                        // Launch target app with 2-minute temporary exception
                        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
                        if (launchIntent != null) {
                            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(launchIntent)
                            Toast.makeText(this@InterventionActivity, "2-Minute Exception Granted", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@InterventionActivity, "2-Minute Exception Active", Toast.LENGTH_SHORT).show()
                        }
                        finish()
                    }
                )
            }
        }
    }
}
