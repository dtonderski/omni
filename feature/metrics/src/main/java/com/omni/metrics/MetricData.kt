package com.omni.metrics

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class MetricCardData(
    val name: String,
    val value: String,
    val unit: String?,
    val periodLabel: String,
    val resolutionLabel: String,
    val aggregationLabel: String,
    val tierLabel: String,
    val nextMilestoneLabel: String,
    val progress: Float,
    val accent: Color,
    val icon: ImageVector
)
