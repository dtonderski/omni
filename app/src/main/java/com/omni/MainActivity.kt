package com.omni

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.omni.core.ui.theme.OmniTheme
import com.omni.metrics.MetricsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OmniTheme {
                MetricsScreen(
                    onOpenGlobalSwitcher = {
                        // TODO: Open the "App Switcher" overlay 
                    }
                )
            }
        }
    }
}