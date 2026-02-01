package com.omni.metrics

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

@Composable
fun MetricsScreen(
    onOpenGlobalSwitcher: () -> Unit
) {

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
            MetricsTopBar(onOpenGlobalSwitcher)
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
        })
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
fun MetricsBottomBar() {
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
                NavPill("Metrics", Icons.Default.List, active = true)
                NavPill("Objectives", Icons.Default.Flag, active = false)
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
