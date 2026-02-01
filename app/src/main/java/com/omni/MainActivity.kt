package com.omni

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.omni.core.ui.theme.OmniTheme
import com.omni.metrics.MetricsScreen
import com.omni.metrics.MetricsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MetricsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OmniTheme {
                MetricsScreen(
                    onOpenGlobalSwitcher = {
                        // TODO: Open the "App Switcher" overlay
                    },
                    metrics = viewModel.metrics
                )
            }
        }
    }
}
