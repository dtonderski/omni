package com.omni.metrics

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.omni.core.ui.components.NavPill
import com.omni.core.ui.components.OmniLogo
import com.omni.core.ui.components.OmniScaffold
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun MetricsScreen(
    onOpenGlobalSwitcher: () -> Unit,
    metrics: StateFlow<List<MetricCardData>>
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedFeature by remember { mutableStateOf(Feature.Metrics) }
    var selectedTab by remember { mutableStateOf(MetricsTab.Metrics) }
    val metricCards by metrics.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Features",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Metrics") },
                    selected = selectedFeature == Feature.Metrics,
                    onClick = {
                        selectedFeature = Feature.Metrics
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Workouts") },
                    selected = selectedFeature == Feature.Workouts,
                    onClick = {
                        selectedFeature = Feature.Workouts
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    icon = { Icon(Icons.Default.FitnessCenter, contentDescription = null) },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    ) {
        OmniScaffold(
            // 1. FEATURE-SPECIFIC TOP BAR
            topBar = {
                MetricsTopBar {
                    scope.launch {
                        drawerState.open()
                    }
                    onOpenGlobalSwitcher()
                }
            },

            // 2. FEATURE-SPECIFIC BOTTOM BAR
            bottomBar = {
                if (selectedFeature == Feature.Metrics) {
                    MetricsBottomBar(
                        selectedTab = selectedTab,
                        onSelectTab = { selectedTab = it }
                    )
                } else {
                    Spacer(modifier = Modifier.height(0.dp))
                }
            },

            // 3. CONTENT
            content = { paddingValues ->
                when (selectedFeature) {
                    Feature.Metrics -> {
                        AnimatedContent(
                            targetState = selectedTab,
                            transitionSpec = {
                                val direction = if (targetState.ordinal > initialState.ordinal) 1 else -1
                                slideInHorizontally { fullWidth -> direction * fullWidth } + fadeIn() togetherWith
                                    slideOutHorizontally { fullWidth -> -direction * fullWidth } + fadeOut()
                            },
                            label = "MetricsTabContent"
                        ) { tab ->
                            when (tab) {
                                MetricsTab.Metrics -> MetricsContent(
                                    dummyMetrics = metricCards,
                                    paddingValues = paddingValues
                                )

                                MetricsTab.Objectives -> ObjectivesContent(paddingValues)
                            }
                        }
                    }

                    Feature.Workouts -> WorkoutsContent(paddingValues)
                }
            }
        )
    }
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
        query = searchQuery,
        onQueryChange = { searchQuery = it },
        onSearch = { isSearchActive = false },
        active = isSearchActive,
        onActiveChange = { isSearchActive = it },
        placeholder = { Text("Search Metrics") },
        shape = CircleShape,
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        ),
        shadowElevation = 12.dp,
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
        trailingIcon = { Icon(Icons.Default.Search, null, Modifier.padding(end = 8.dp)) }) {
        Text("Results...", Modifier.padding(16.dp))
    }
}

@Composable
fun MetricsBottomBar(
    selectedTab: MetricsTab,
    onSelectTab: (MetricsTab) -> Unit
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
                    Icons.Default.List,
                    active = selectedTab == MetricsTab.Metrics,
                    onClick = { onSelectTab(MetricsTab.Metrics) }
                )
                NavPill(
                    "Objectives",
                    Icons.Default.Flag,
                    active = selectedTab == MetricsTab.Objectives,
                    onClick = { onSelectTab(MetricsTab.Objectives) }
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

@Composable
private fun WorkoutsContent(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Workouts coming soon",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

enum class Feature {
    Metrics,
    Workouts
}

enum class MetricsTab {
    Metrics,
    Objectives
}
