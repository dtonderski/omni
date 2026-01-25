package com.omni.metrics

import androidx.compose.ui.graphics.vector.ImageVector

data class MetricData(
    val label: String,
    val value: String,
    val subtitle: String,
    val icon: ImageVector
)