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
import com.omni.metrics.data.DisplayAggregationType
import com.omni.metrics.data.EntryDao
import com.omni.metrics.data.EntryEntity
import com.omni.metrics.data.MetricDao
import com.omni.metrics.data.MetricEntity
import com.omni.metrics.data.MetricKind
import com.omni.metrics.data.MetricResolution
import com.omni.metrics.data.MetricType
import com.omni.metrics.data.MilestoneDao
import com.omni.metrics.data.ObjectiveDao
import com.omni.metrics.data.ObjectiveEntity
import com.omni.metrics.data.ObjectiveAggregationType
import com.omni.metrics.data.ObjectivePolarity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MetricsViewModel @Inject constructor(
    private val metricDao: MetricDao,
    private val entryDao: EntryDao,
    private val objectiveDao: ObjectiveDao,
    private val milestoneDao: MilestoneDao
) : ViewModel() {

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val metrics: StateFlow<List<MetricCardData>> = metricDao.observeAll()
        .flatMapLatest { entities ->
            if (entities.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(entities.map { entity ->
                    entity.toCardDataFlow(entryDao, objectiveDao, milestoneDao)
                }) { cards ->
                    cards.toList()
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addMetric(
        name: String,
        kind: MetricKind,
        type: MetricType,
        unit: String,
        resolution: MetricResolution,
        displayResolution: MetricResolution,
        displayAggregation: DisplayAggregationType,
        iconKey: String,
        accentColor: Int
    ) {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) return
        val now = System.currentTimeMillis()
        viewModelScope.launch {
            metricDao.upsert(
                MetricEntity(
                    name = trimmedName,
                    kind = kind,
                    type = type,
                    unit = unit.trim(),
                    currentResolution = resolution,
                    displayResolution = displayResolution,
                    displayAggregation = displayAggregation,
                    maxValueForColor = null,
                    accentColor = accentColor,
                    iconKey = iconKey,
                    createdAt = now
                )
            )
        }
    }
}

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
private fun MetricEntity.toCardDataFlow(entryDao: EntryDao, objectiveDao: ObjectiveDao, milestoneDao: MilestoneDao) =
    when (kind) {
        MetricKind.STATE -> combine(
            entryDao.observeLatest(id),
            objectiveDao.observeByMetric(id)
        ) { entry, objectives ->
            Pair(entry, objectives)
        }.flatMapLatest { (entry, objectives) ->
            objectiveSummaryFlow(objectives, milestoneDao).map { summary ->
                toStateCard(entry, summary)
            }
        }
        MetricKind.EVENT -> {
            val range = displayResolution.lastCompletedRange(LocalDate.now())
            combine(
                entryDao.observeEntriesForRange(id, range.start, range.end),
                objectiveDao.observeByMetric(id)
            ) { entries, objectives ->
                Pair(entries, objectives)
            }.flatMapLatest { (entries, objectives) ->
                objectiveSummaryFlow(objectives, milestoneDao).map { summary ->
                    toEventCard(entries, summary)
                }
            }
        }
    }

private fun MetricEntity.toStateCard(entry: EntryEntity?, objective: ObjectiveSummary?): MetricCardData {
    val objectiveUi = objective?.toUi(entry.toNumericValue(type))
    val useGradient = objectiveUi?.hasTier == true
    val (accentStart, accentEnd) = if (useGradient) {
        (objectiveUi.tierGradientStart ?: NO_TIER_COLOR) to (objectiveUi.tierGradientEnd ?: NO_TIER_COLOR)
    } else {
        val base = metricTypeColor(type)
        base to base
    }
    val value = entry.toDisplayValue(type)
    return MetricCardData(
        name = name,
        value = value,
        unit = unit,
        displayPeriodLabel = "Latest",
        displayAggregationLabel = "Latest",
        logResolutionLabel = "Logs ${currentResolution.toDisplayLabel().lowercase()}",
        tierLabel = objectiveUi?.tierLabel,
        tierGradientStart = objectiveUi?.tierGradientStart,
        tierGradientEnd = objectiveUi?.tierGradientEnd,
        nextTierLabel = objectiveUi?.nextTierLabel,
        nextMilestoneLabel = objectiveUi?.nextMilestoneLabel,
        progress = objectiveUi?.progress,
        progressSegments = objectiveUi?.progressSegments,
        segmentWeights = objectiveUi?.segmentWeights,
        objectivePeriodLabel = objectiveUi?.objectivePeriodLabel,
        objectiveAggregationLabel = objectiveUi?.objectiveAggregationLabel,
        useGradient = useGradient,
        accentStart = accentStart,
        accentEnd = accentEnd,
        icon = iconKey.toIcon()
    )
}

private fun MetricEntity.toEventCard(entries: List<EntryEntity>, objective: ObjectiveSummary?): MetricCardData {
    val objectiveUi = objective?.toUi(entries.toNumericValue(type, displayAggregation))
    val useGradient = objectiveUi?.hasTier == true
    val (accentStart, accentEnd) = if (useGradient) {
        (objectiveUi.tierGradientStart ?: NO_TIER_COLOR) to (objectiveUi.tierGradientEnd ?: NO_TIER_COLOR)
    } else {
        val base = metricTypeColor(type)
        base to base
    }
    val value = entries.toDisplayValue(type, displayAggregation)
    return MetricCardData(
        name = name,
        value = value,
        unit = unit,
        displayPeriodLabel = displayResolution.toHeadlinePeriodLabel(),
        displayAggregationLabel = displayAggregation.toDisplayLabel(),
        logResolutionLabel = "Logs ${currentResolution.toDisplayLabel().lowercase()}",
        tierLabel = objectiveUi?.tierLabel,
        tierGradientStart = objectiveUi?.tierGradientStart,
        tierGradientEnd = objectiveUi?.tierGradientEnd,
        nextTierLabel = objectiveUi?.nextTierLabel,
        nextMilestoneLabel = objectiveUi?.nextMilestoneLabel,
        progress = objectiveUi?.progress,
        progressSegments = objectiveUi?.progressSegments,
        segmentWeights = objectiveUi?.segmentWeights,
        objectivePeriodLabel = objectiveUi?.objectivePeriodLabel,
        objectiveAggregationLabel = objectiveUi?.objectiveAggregationLabel,
        useGradient = useGradient,
        accentStart = accentStart,
        accentEnd = accentEnd,
        icon = iconKey.toIcon()
    )
}

private fun MetricResolution.toDisplayLabel(): String = when (this) {
    MetricResolution.DAILY -> "Daily"
    MetricResolution.WEEKLY -> "Weekly"
    MetricResolution.MONTHLY -> "Monthly"
    MetricResolution.YEARLY -> "Yearly"
}

private fun MetricResolution.toHeadlinePeriodLabel(): String = when (this) {
    MetricResolution.DAILY -> "Last week"
    MetricResolution.WEEKLY -> "Last month"
    MetricResolution.MONTHLY -> "Last year"
    MetricResolution.YEARLY -> "Last year"
}

private fun DisplayAggregationType.toDisplayLabel(): String = when (this) {
    DisplayAggregationType.TOTAL -> "Total"
    DisplayAggregationType.AVERAGE -> "Average"
    DisplayAggregationType.LATEST -> "Latest"
}

private fun ObjectiveAggregationType.toDisplayLabel(): String = when (this) {
    ObjectiveAggregationType.TOTAL -> "Total"
    ObjectiveAggregationType.AVERAGE -> "Average"
    ObjectiveAggregationType.LATEST -> "Latest"
}

private fun LocalDate.toObjectivePeriodLabel(end: LocalDate): String {
    return when {
        isSameYear(end) && isWholeYear(end) -> "Yearly"
        isSameMonth(end) && isWholeMonth(end) -> "Monthly"
        isSameWeek(end) && isWholeWeek(end) -> "Weekly"
        else -> "Custom"
    }
}

private fun LocalDate.isSameYear(other: LocalDate) = year == other.year

private fun LocalDate.isSameMonth(other: LocalDate) = year == other.year && month == other.month

private fun LocalDate.isSameWeek(other: LocalDate): Boolean {
    val thisWeekStart = this.with(DayOfWeek.MONDAY)
    val otherWeekStart = other.with(DayOfWeek.MONDAY)
    return thisWeekStart == otherWeekStart
}

private fun LocalDate.isWholeYear(end: LocalDate): Boolean =
    this == LocalDate.of(year, 1, 1) && end == LocalDate.of(year, 12, 31)

private fun LocalDate.isWholeMonth(end: LocalDate): Boolean =
    dayOfMonth == 1 && end == this.plusMonths(1).minusDays(1)

private fun LocalDate.isWholeWeek(end: LocalDate): Boolean =
    dayOfWeek == DayOfWeek.MONDAY && end == this.plusDays(6)

private data class DateRange(val start: LocalDate, val end: LocalDate)

private fun MetricResolution.lastCompletedRange(today: LocalDate): DateRange = when (this) {
    MetricResolution.DAILY -> {
        val day = today.minusDays(1)
        DateRange(day, day)
    }
    MetricResolution.WEEKLY -> {
        val lastWeekStart = today.minusWeeks(1).with(DayOfWeek.MONDAY)
        DateRange(lastWeekStart, lastWeekStart.plusDays(6))
    }
    MetricResolution.MONTHLY -> {
        val start = today.minusMonths(1).withDayOfMonth(1)
        DateRange(start, start.plusMonths(1).minusDays(1))
    }
    MetricResolution.YEARLY -> {
        val year = today.minusYears(1)
        val start = LocalDate.of(year.year, 1, 1)
        val end = LocalDate.of(year.year, 12, 31)
        DateRange(start, end)
    }
}

private fun List<EntryEntity>.toDisplayValue(
    type: MetricType,
    displayAggregation: DisplayAggregationType
): String {
    return when (displayAggregation) {
        DisplayAggregationType.LATEST -> {
            if (isEmpty()) return "—"
            val latest = maxByOrNull { it.periodEnd } ?: return "—"
            latest.toDisplayValue(type)
        }
        DisplayAggregationType.TOTAL -> {
            if (isEmpty()) return "0"
            when (type) {
                MetricType.INT -> sumOf { it.valueInt ?: 0 }.toString()
                MetricType.BOOLEAN -> count { it.valueBool == true }.toString()
            }
        }
        DisplayAggregationType.AVERAGE -> {
            if (isEmpty()) return "0"
            when (type) {
                MetricType.INT -> averageInt()
                MetricType.BOOLEAN -> averageBoolean()
            }
        }
    }
}

private fun List<EntryEntity>.toNumericValue(
    type: MetricType,
    displayAggregation: DisplayAggregationType
): Double? {
    if (isEmpty()) return null
    return when (displayAggregation) {
        DisplayAggregationType.LATEST -> maxByOrNull { it.periodEnd }?.toNumericValue(type)
        DisplayAggregationType.TOTAL -> when (type) {
            MetricType.INT -> sumOf { it.valueInt ?: 0 }.toDouble()
            MetricType.BOOLEAN -> count { it.valueBool == true }.toDouble()
        }
        DisplayAggregationType.AVERAGE -> when (type) {
            MetricType.INT -> mapNotNull { it.valueInt?.toDouble() }.averageOrNull()
            MetricType.BOOLEAN -> mapNotNull { it.valueBool?.let { v -> if (v) 1.0 else 0.0 } }.averageOrNull()
        }
    }
}

private fun EntryEntity?.toNumericValue(type: MetricType): Double? {
    if (this == null) return null
    return when (type) {
        MetricType.INT -> valueInt?.toDouble()
        MetricType.BOOLEAN -> valueBool?.let { if (it) 1.0 else 0.0 }
    }
}

private fun List<EntryEntity>.averageInt(): String {
    val values = mapNotNull { it.valueInt?.toDouble() }
    if (values.isEmpty()) return "—"
    val avg = values.average()
    return if (avg % 1.0 == 0.0) avg.toInt().toString() else String.format("%.1f", avg)
}

private fun List<EntryEntity>.averageBoolean(): String {
    val values = mapNotNull { it.valueBool?.let { v -> if (v) 1.0 else 0.0 } }
    if (values.isEmpty()) return "—"
    val percent = (values.average() * 100).toInt()
    return "${percent}%"
}

private fun EntryEntity?.toDisplayValue(type: MetricType): String {
    if (this == null) return "—"
    return when (type) {
        MetricType.INT -> valueInt?.toString() ?: "—"
        MetricType.BOOLEAN -> valueBool?.let { if (it) "Yes" else "No" } ?: "—"
    }
}

private fun List<Double>.averageOrNull(): Double? {
    if (isEmpty()) return null
    return average()
}

private data class ObjectiveSummary(
    val name: String,
    val evaluationStart: LocalDate,
    val evaluationEnd: LocalDate,
    val aggregationType: ObjectiveAggregationType,
    val polarity: ObjectivePolarity,
    val milestones: List<MilestoneSnapshot>
)

private data class MilestoneSnapshot(
    val name: String,
    val thresholdValue: Long,
    val rank: Int
)

private data class ObjectiveUi(
    val tierLabel: String,
    val nextMilestoneLabel: String,
    val nextTierLabel: String?,
    val nextTierColor: Color?,
    val progress: Float,
    val progressSegments: List<Float>,
    val segmentWeights: List<Float>,
    val tierGradientStart: Color?,
    val tierGradientEnd: Color?,
    val hasTier: Boolean,
    val objectivePeriodLabel: String,
    val objectiveAggregationLabel: String
)

private fun objectiveSummaryFlow(
    objectives: List<ObjectiveEntity>,
    milestoneDao: MilestoneDao
) = when {
    objectives.isEmpty() -> flowOf(null)
    else -> {
        val objective = objectives.first()
        milestoneDao.observeByObjective(objective.id).map { milestones ->
            ObjectiveSummary(
                name = objective.name,
                evaluationStart = objective.evaluationStart,
                evaluationEnd = objective.evaluationEnd,
                aggregationType = objective.aggregationType,
                polarity = objective.polarity,
                milestones = milestones.map {
                    MilestoneSnapshot(it.name, it.thresholdValue, it.rank)
                }
            )
        }
    }
}

private fun ObjectiveSummary.toUi(currentValue: Double?): ObjectiveUi? {
    if (milestones.isEmpty() || currentValue == null) return null
    val sorted = milestones.sortedBy { it.thresholdValue }
    val deltas = sorted.mapIndexed { index, milestone ->
        val previous = if (index == 0) 0L else sorted[index - 1].thresholdValue
        (milestone.thresholdValue - previous).coerceAtLeast(1)
    }
    val total = sorted.last().thresholdValue.toDouble().coerceAtLeast(1.0)
    val segmentWeights = deltas.map { it.toFloat() }
    val segmentFills = sorted.mapIndexed { index, milestone ->
        val start = if (index == 0) 0.0 else sorted[index - 1].thresholdValue.toDouble()
        val end = milestone.thresholdValue.toDouble()
        ((currentValue - start) / (end - start)).coerceIn(0.0, 1.0).toFloat()
    }
    val progress = (currentValue / total).coerceIn(0.0, 1.0).toFloat()
    val achieved = sorted.lastOrNull { currentValue >= it.thresholdValue }
    val next = sorted.firstOrNull { currentValue < it.thresholdValue }
    val tierLabel = achieved?.name ?: "No tier"
    val nextLabel = next?.name?.let { "Next: $it" } ?: "Top tier"
    val tierGradient = achieved?.name?.let { medalGradient(it) }
    val tierGradientStart = tierGradient?.first ?: NO_TIER_COLOR
    val tierGradientEnd = tierGradient?.second ?: NO_TIER_COLOR
    val nextTierColor = next?.name?.let { medalGradient(it).first } ?: tierGradientStart
    return ObjectiveUi(
        tierLabel = tierLabel,
        nextMilestoneLabel = nextLabel,
        nextTierLabel = next?.name,
        nextTierColor = nextTierColor,
        progress = progress,
        progressSegments = segmentFills,
        segmentWeights = segmentWeights,
        tierGradientStart = tierGradientStart,
        tierGradientEnd = tierGradientEnd,
        hasTier = achieved != null,
        objectivePeriodLabel = evaluationStart.toObjectivePeriodLabel(evaluationEnd),
        objectiveAggregationLabel = aggregationType.toDisplayLabel()
    )
}

private val NO_TIER_COLOR = Color(0xFF7F8FA6)

private fun medalGradient(name: String): Pair<Color, Color> = when (name.lowercase()) {
    "bronze" -> Color(0xFFE17055) to Color(0xFFFAB1A0)
    "silver" -> Color(0xFFB9C1C6) to Color(0xFFF2F4F5)
    "gold" -> Color(0xFFFDCB6E) to Color(0xFFFFEAA7)
    "diamond" -> Color(0xFF0284C7) to Color(0xFF5BC0DE)
    else -> NO_TIER_COLOR to NO_TIER_COLOR
}

private fun metricTypeColor(type: MetricType): Color = when (type) {
    MetricType.BOOLEAN -> Color(0xFF00B894)
    MetricType.INT -> Color(0xFF6C5CE7)
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
