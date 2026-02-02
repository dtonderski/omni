package com.omni.metrics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.*
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.omni.core.ui.components.NavPill
import com.omni.core.ui.components.OmniLogo
import com.omni.core.ui.components.OmniScaffold
import com.omni.metrics.data.DisplayAggregationType
import com.omni.metrics.data.MetricKind
import com.omni.metrics.data.MetricResolution
import com.omni.metrics.data.MetricType
import kotlinx.coroutines.flow.StateFlow

@Composable
fun MetricsEntry(
    onOpenGlobalSwitcher: () -> Unit,
    metrics: StateFlow<List<MetricCardData>>,
    onAddMetric: (
        name: String,
        kind: MetricKind,
        type: MetricType,
        unit: String,
        resolution: MetricResolution,
        displayResolution: MetricResolution,
        displayAggregation: DisplayAggregationType,
        iconKey: String,
        accentColor: Int
    ) -> Unit
) {
    var selectedDestination by remember { mutableStateOf(MetricsTab.Metrics) }
    var isAddSheetOpen by remember { mutableStateOf(false) }
    val metricCards by metrics.collectAsState()

    OmniScaffold(
        topBar = { MetricsTopBar(onOpenGlobalSwitcher, selectedDestination) },
        bottomBar = {
            MetricsBottomBar(
                selectedDestination = selectedDestination,
                onSelectDestination = { destination -> selectedDestination = destination },
                onAddMetric = { isAddSheetOpen = true }
            )
        },
        content = { paddingValues ->
            AnimatedContent(
                targetState = selectedDestination,
                transitionSpec = {
                    val direction = if (targetState.ordinal > initialState.ordinal) 1 else -1
                    slideInHorizontally(
                        initialOffsetX = { it * direction },
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(150)) togetherWith
                        slideOutHorizontally(
                            targetOffsetX = { -it * direction },
                            animationSpec = tween(300)
                        ) + fadeOut(animationSpec = tween(150))
                },
                label = "MetricsTabTransition"
            ) { destination ->
                when (destination) {
                    MetricsTab.Metrics -> MetricsContent(
                        dummyMetrics = metricCards,
                        paddingValues = paddingValues
                    )

                    MetricsTab.Objectives -> ObjectivesContent(paddingValues)
                }
            }

            if (isAddSheetOpen) {
                AddMetricBottomSheet(
                    onDismiss = { isAddSheetOpen = false },
                    onCreate = { state ->
                        onAddMetric(
                            state.name.trim(),
                            state.kind,
                            state.type,
                            state.unit.trim(),
                            state.resolution,
                            state.displayResolution,
                            state.displayAggregation,
                            state.iconKey,
                            state.accentColor
                        )
                        isAddSheetOpen = false
                    }
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetricsTopBar(onOpenGlobalSwitcher: () -> Unit, activeTab: MetricsTab) {
    var searchQuery by remember { mutableStateOf("") }
    val placeholderText = if (activeTab == MetricsTab.Metrics) {
        "Search Metrics"
    } else {
        "Search Objectives"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .shadow(
                    elevation = 14.dp,
                    shape = CircleShape,
                    clip = false
                )
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
        )
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
            modifier = Modifier.matchParentSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OmniLogo(onClick = onOpenGlobalSwitcher)
                VerticalDivider(
                    modifier = Modifier
                        .height(24.dp)
                        .padding(horizontal = 8.dp)
                )
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(placeholderText) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun MetricsBottomBar(
    selectedDestination: MetricsTab,
    onSelectDestination: (MetricsTab) -> Unit,
    onAddMetric: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.98f),
            shadowElevation = 10.dp,
            modifier = Modifier.height(58.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                NavPill(
                    "Metrics",
                    Icons.AutoMirrored.Filled.List,
                    active = selectedDestination == MetricsTab.Metrics,
                    onClick = { onSelectDestination(MetricsTab.Metrics) }
                )
                NavPill(
                    "Objectives",
                    Icons.Default.Flag,
                    active = selectedDestination == MetricsTab.Objectives,
                    onClick = { onSelectDestination(MetricsTab.Objectives) }
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        FilledIconButton(
            onClick = onAddMetric,
            modifier = Modifier.size(64.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

private data class AddMetricState(
    val name: String,
    val unit: String,
    val kind: MetricKind,
    val type: MetricType,
    val resolution: MetricResolution,
    val displayResolution: MetricResolution,
    val displayAggregation: DisplayAggregationType,
    val iconKey: String,
    val accentColor: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddMetricBottomSheet(
    onDismiss: () -> Unit,
    onCreate: (AddMetricState) -> Unit
) {
    val iconOptions = remember {
        listOf(
            "directions_run" to Icons.AutoMirrored.Filled.DirectionsRun,
            "restaurant" to Icons.Default.Restaurant,
            "nights_stay" to Icons.Default.NightsStay,
            "code" to Icons.Default.Code,
            "savings" to Icons.Default.Savings,
            "monitor_weight" to Icons.Default.MonitorWeight,
            "people" to Icons.Default.People,
            "track" to Icons.Default.TrackChanges
        )
    }
    val colorOptions = remember {
        listOf(
            0xFFFFA24A.toInt(),
            0xFF7AC6FF.toInt(),
            0xFF9B8CFF.toInt(),
            0xFF4CC7B3.toInt(),
            0xFFE8A860.toInt(),
            0xFF67D18C.toInt(),
            0xFFFF8FA3.toInt()
        )
    }

    var name by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var kind by remember { mutableStateOf(MetricKind.EVENT) }
    var type by remember { mutableStateOf(MetricType.BOOLEAN) }
    var resolution by remember { mutableStateOf(MetricResolution.DAILY) }
    var displayResolution by remember { mutableStateOf(MetricResolution.DAILY.nextBigger()) }
    var displayAggregation by remember { mutableStateOf(DisplayAggregationType.TOTAL) }
    var iconKey by remember { mutableStateOf(iconOptions.first().first) }
    var accentColor by remember { mutableStateOf(colorOptions.first()) }

    val isValid = name.isNotBlank()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
                    Text(
                        text = "Create a metric",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        shape = RoundedCornerShape(18.dp),
                        shadowElevation = 4.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                color = Color(accentColor),
                                shape = CircleShape,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    val previewIcon = iconOptions.firstOrNull { it.first == iconKey }?.second
                                    if (previewIcon != null) {
                                        Icon(
                                            previewIcon,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                            Column {
                                Text(
                                    text = if (name.isBlank()) "New metric" else name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = if (unit.isBlank()) "No unit" else unit,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Metric name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unit (optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Kind",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = kind == MetricKind.EVENT,
                            onClick = {
                                kind = MetricKind.EVENT
                                displayResolution = resolution.nextBigger()
                                displayAggregation = DisplayAggregationType.TOTAL
                            },
                            label = { Text("Event") }
                        )
                        FilterChip(
                            selected = kind == MetricKind.STATE,
                            onClick = {
                                kind = MetricKind.STATE
                                displayResolution = resolution
                                displayAggregation = DisplayAggregationType.LATEST
                            },
                            label = { Text("State") }
                        )
                    }

                    Text(
                        text = "Type",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = type == MetricType.BOOLEAN,
                            onClick = { type = MetricType.BOOLEAN },
                            label = { Text("Boolean") }
                        )
                        FilterChip(
                            selected = type == MetricType.INT,
                            onClick = { type = MetricType.INT },
                            label = { Text("Number") }
                        )
                    }

                    Text(
                        text = "Resolution",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = resolution == MetricResolution.DAILY,
                            onClick = {
                                resolution = MetricResolution.DAILY
                                if (kind == MetricKind.EVENT) {
                                    displayResolution = MetricResolution.DAILY.nextBigger()
                                } else {
                                    displayResolution = MetricResolution.DAILY
                                }
                            },
                            label = { Text("Daily") }
                        )
                        FilterChip(
                            selected = resolution == MetricResolution.WEEKLY,
                            onClick = {
                                resolution = MetricResolution.WEEKLY
                                if (kind == MetricKind.EVENT) {
                                    displayResolution = MetricResolution.WEEKLY.nextBigger()
                                } else {
                                    displayResolution = MetricResolution.WEEKLY
                                }
                            },
                            label = { Text("Weekly") }
                        )
                        FilterChip(
                            selected = resolution == MetricResolution.MONTHLY,
                            onClick = {
                                resolution = MetricResolution.MONTHLY
                                if (kind == MetricKind.EVENT) {
                                    displayResolution = MetricResolution.MONTHLY.nextBigger()
                                } else {
                                    displayResolution = MetricResolution.MONTHLY
                                }
                            },
                            label = { Text("Monthly") }
                        )
                    }

                    if (kind == MetricKind.EVENT) {
                        Text(
                            text = "Display window",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = displayResolution == MetricResolution.DAILY,
                                onClick = { displayResolution = MetricResolution.DAILY },
                                label = { Text("Daily") }
                            )
                            FilterChip(
                                selected = displayResolution == MetricResolution.WEEKLY,
                                onClick = { displayResolution = MetricResolution.WEEKLY },
                                label = { Text("Weekly") }
                            )
                            FilterChip(
                                selected = displayResolution == MetricResolution.MONTHLY,
                                onClick = { displayResolution = MetricResolution.MONTHLY },
                                label = { Text("Monthly") }
                            )
                            FilterChip(
                                selected = displayResolution == MetricResolution.YEARLY,
                                onClick = { displayResolution = MetricResolution.YEARLY },
                                label = { Text("Yearly") }
                            )
                        }

                        Text(
                            text = "Aggregation",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = displayAggregation == DisplayAggregationType.TOTAL,
                                onClick = { displayAggregation = DisplayAggregationType.TOTAL },
                                label = { Text("Total") }
                            )
                            FilterChip(
                                selected = displayAggregation == DisplayAggregationType.AVERAGE,
                                onClick = { displayAggregation = DisplayAggregationType.AVERAGE },
                                label = { Text("Average") }
                            )
                            FilterChip(
                                selected = displayAggregation == DisplayAggregationType.LATEST,
                                onClick = { displayAggregation = DisplayAggregationType.LATEST },
                                label = { Text("Latest") }
                            )
                        }
                    }

                    Text(
                        text = "Icon",
                        style = MaterialTheme.typography.titleSmall
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(iconOptions) { (key, icon) ->
                            FilterChip(
                                selected = iconKey == key,
                                onClick = { iconKey = key },
                                label = { Icon(icon, contentDescription = null) }
                            )
                        }
                    }

                    Text(
                        text = "Accent color",
                        style = MaterialTheme.typography.titleSmall
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(colorOptions) { colorInt ->
                            val color = Color(colorInt)
                            val isSelected = accentColor == colorInt
                            Surface(
                                color = color,
                                shape = CircleShape,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape),
                                onClick = { accentColor = colorInt }
                            ) {
                                if (isSelected) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        onCreate(
                            AddMetricState(
                                name = name,
                                unit = unit,
                                kind = kind,
                                type = type,
                                resolution = resolution,
                                displayResolution = displayResolution,
                                displayAggregation = displayAggregation,
                                iconKey = iconKey,
                                accentColor = accentColor
                            )
                        )
                    },
                    enabled = isValid,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Text("Create")
                }
            }
        }
    }
}

@Composable
private fun MetricsContent(
    dummyMetrics: List<MetricCardData>,
    paddingValues: PaddingValues
) {
    LazyColumn(
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        items(dummyMetrics) { metric ->
            PremiumMetricCard(metric)
        }
    }
}

@Composable
private fun ObjectivesContent(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Objectives coming soon",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

enum class MetricsTab {
    Metrics,
    Objectives
}

private fun MetricResolution.nextBigger(): MetricResolution = when (this) {
    MetricResolution.DAILY -> MetricResolution.WEEKLY
    MetricResolution.WEEKLY -> MetricResolution.MONTHLY
    MetricResolution.MONTHLY -> MetricResolution.YEARLY
    MetricResolution.YEARLY -> MetricResolution.YEARLY
}
