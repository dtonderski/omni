package com.omni.metrics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetricsScreen(
    onOpenGlobalSwitcher: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    // DATA (Eventually this comes from ViewModel)
    val dummyMetrics = remember {
        listOf(
            MetricData("Workouts", "14", "Sessions this month", Icons.Default.DirectionsRun),
            MetricData("Protein", "145g", "Avg daily intake", Icons.Default.Restaurant),
            MetricData("Family", "3", "Visits this year", Icons.Default.People),
            MetricData("Mood", "Great", "Current vibe", Icons.Default.SentimentSatisfiedAlt),
            MetricData("Sleep", "7.5h", "Avg duration", Icons.Default.NightsStay),
            MetricData("Water", "2.1L", "Daily average", Icons.Default.LocalDrink),
            MetricData("Steps", "12k", "Daily target", Icons.Default.DirectionsWalk),
            MetricData("Focus", "4h", "Deep work avg", Icons.Default.Timer),
            MetricData("Focus", "4h", "Deep work avg", Icons.Default.Timer),
            MetricData("Focus", "4h", "Deep work avg", Icons.Default.Timer),
            MetricData("Focus", "4h", "Deep work avg", Icons.Default.Timer),
            MetricData("Focus", "4h", "Deep work avg", Icons.Default.Timer),
            MetricData("Focus", "4h", "Deep work avg", Icons.Default.Timer),
            MetricData("Focus", "4h", "Deep work avg", Icons.Default.Timer),
            MetricData("Focus", "4h", "Deep work avg", Icons.Default.Timer),
            MetricData("Focus", "4h", "Deep work avg", Icons.Default.Timer),
            MetricData("Focus", "4h", "Deep work avg", Icons.Default.Timer),

            MetricData("Reading", "12", "Pages today", Icons.Default.MenuBook)
        )
    }

    OmniScaffold(
        // 1. FEATURE-SPECIFIC TOP BAR
        topBar = {
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
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.98f)
                ),
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
            ) {
                Text("Results...", Modifier.padding(16.dp))
            }
        },

        // 2. FEATURE-SPECIFIC BOTTOM BAR
        bottomBar = {
            MetricsBottomBar()
        },

        // 3. CONTENT
        content = { paddingValues ->
            LazyColumn(
                contentPadding = paddingValues, // Uses the safe-zones from Scaffold
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
    )
}

@Composable
fun MetricsBottomBar() {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.96f),
        shadowElevation = 12.dp,
        modifier = Modifier.height(64.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NavPill("Explore", Icons.Default.Explore, active = true)
            NavPill("Saved", Icons.Default.Bookmark, active = false)
            VerticalDivider(modifier = Modifier
                .height(24.dp)
                .padding(horizontal = 4.dp))
            FilledIconButton(
                onClick = { },
                modifier = Modifier.size(48.dp),
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    }
}