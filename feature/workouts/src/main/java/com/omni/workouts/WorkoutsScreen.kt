package com.omni.workouts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.omni.core.ui.components.OmniScaffold

@Composable
fun WorkoutsEntry(onOpenGlobalSwitcher: () -> Unit) {
    OmniScaffold(
        topBar = { WorkoutsTopBar(onOpenGlobalSwitcher) },
        bottomBar = { Spacer(modifier = Modifier.height(0.dp)) },
        content = { paddingValues ->
            WorkoutsContent(paddingValues)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkoutsTopBar(onOpenGlobalSwitcher: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text("Workouts") },
        navigationIcon = {
            IconButton(onClick = onOpenGlobalSwitcher) {
                Icon(Icons.Default.Menu, contentDescription = "Open menu")
            }
        }
    )
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
