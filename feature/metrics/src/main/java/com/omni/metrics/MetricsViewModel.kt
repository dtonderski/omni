package com.omni.metrics

import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrackChanges
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omni.metrics.data.MetricDao
import com.omni.metrics.data.MetricEntity
import com.omni.metrics.data.MetricResolution
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MetricsViewModel @Inject constructor(
    metricDao: MetricDao
) : ViewModel() {

    val metrics: StateFlow<List<MetricCardData>> = metricDao.observeAll()
        .map { entities -> entities.map { it.toCardData() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

private fun MetricEntity.toCardData(): MetricCardData {
    val accent = accentColor?.let { Color(it) } ?: Color(0xFF9AA0A6)
    return MetricCardData(
        name = name,
        value = "0",
        unit = unit,
        periodLabel = currentResolution.toPeriodLabel(),
        resolutionLabel = currentResolution.toDisplayLabel(),
        aggregationLabel = "Tracking",
        tierLabel = "No tier",
        nextMilestoneLabel = "Add objectives",
        progress = 0f,
        accent = accent,
        icon = iconKey.toIcon()
    )
}

private fun MetricResolution.toDisplayLabel(): String = when (this) {
    MetricResolution.DAILY -> "Daily"
    MetricResolution.WEEKLY -> "Weekly"
    MetricResolution.MONTHLY -> "Monthly"
    MetricResolution.YEARLY -> "Yearly"
}

private fun MetricResolution.toPeriodLabel(): String = when (this) {
    MetricResolution.DAILY -> "Today"
    MetricResolution.WEEKLY -> "This week"
    MetricResolution.MONTHLY -> "This month"
    MetricResolution.YEARLY -> "This year"
}

private fun String?.toIcon() = when (this) {
    "directions_run" -> Icons.AutoMirrored.Filled.DirectionsRun
    "restaurant" -> Icons.Default.Restaurant
    "nights_stay" -> Icons.Default.NightsStay
    "code" -> Icons.Default.Code
    "savings" -> Icons.Default.Savings
    "monitor_weight" -> Icons.Default.MonitorWeight
    "people" -> Icons.Default.People
    else -> Icons.Default.TrackChanges
}
