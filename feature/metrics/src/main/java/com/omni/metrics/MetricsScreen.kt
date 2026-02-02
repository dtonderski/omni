package com.omni.metrics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.omni.core.ui.components.NavPill
import com.omni.core.ui.components.OmniLogo
import com.omni.core.ui.components.OmniScaffold
import kotlinx.coroutines.flow.StateFlow

@Composable
fun MetricsEntry(
    onOpenGlobalSwitcher: () -> Unit,
    metrics: StateFlow<List<MetricCardData>>
) {
    var selectedDestination by remember { mutableStateOf(MetricsTab.Metrics) }
    val metricCards by metrics.collectAsState()

    OmniScaffold(
        topBar = { MetricsTopBar(onOpenGlobalSwitcher) },
        bottomBar = {
            MetricsBottomBar(
                selectedDestination = selectedDestination,
                onSelectDestination = { destination -> selectedDestination = destination }
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
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetricsTopBar(onOpenGlobalSwitcher: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    DockedSearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, CircleShape),
        inputField = {
            SearchBarDefaults.InputField(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = { isSearchActive = false },
                expanded = isSearchActive,
                onExpandedChange = { isSearchActive = it },
                placeholder = { Text("Search Metrics") },
                leadingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OmniLogo(onClick = onOpenGlobalSwitcher)
                        VerticalDivider(
                            modifier = Modifier
                                .height(24.dp)
                                .padding(horizontal = 8.dp)
                        )
                    }
                },
                trailingIcon = { Icon(Icons.Default.Search, null, Modifier.padding(end = 8.dp)) }
            )
        },
        expanded = isSearchActive,
        onExpandedChange = { isSearchActive = it },
        shape = CircleShape,
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        ),
        shadowElevation = 12.dp
    ) {
        Text("Results...", Modifier.padding(16.dp))
    }
}

@Composable
fun MetricsBottomBar(
    selectedDestination: MetricsTab,
    onSelectDestination: (MetricsTab) -> Unit
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
            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.98f),
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
            onClick = { },
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
