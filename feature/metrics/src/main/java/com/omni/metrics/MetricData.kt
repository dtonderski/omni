package com.omni.metrics

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class MetricCardData(
    val name: String,
    val value: String,
    val unit: String?,
    val displayPeriodLabel: String,
    val displayAggregationLabel: String,
    val logResolutionLabel: String,
    val tierLabel: String?,
    val tierGradientStart: Color?,
    val tierGradientEnd: Color?,
    val nextTierLabel: String?,
    val nextMilestoneLabel: String?,
    val progress: Float?,
    val progressSegments: List<Float>?,
    val segmentWeights: List<Float>?,
    val objectivePeriodLabel: String?,
    val objectiveAggregationLabel: String?,
    val useGradient: Boolean,
    val accentStart: Color,
    val accentEnd: Color,
    val icon: ImageVector
)
