package com.omni.core.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.List
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
sealed interface RootDestination : NavKey {
    @Serializable
    data object Metrics : RootDestination

    @Serializable
    data object Workouts : RootDestination
}

@Composable
fun OmniRootNavHost(
    metricsEntry: @Composable (onOpenGlobalSwitcher: () -> Unit) -> Unit,
    workoutsEntry: @Composable (onOpenGlobalSwitcher: () -> Unit) -> Unit
) {
    val rootBackStack = rememberNavBackStack(RootDestination.Metrics)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val selectedDestination = rootBackStack.lastOrNull() as? RootDestination ?: RootDestination.Metrics

    val openGlobalSwitcher: () -> Unit = {
        scope.launch { drawerState.open() }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Features",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Metrics") },
                    selected = selectedDestination == RootDestination.Metrics,
                    onClick = {
                        if (selectedDestination != RootDestination.Metrics) {
                            rootBackStack.clear()
                            rootBackStack.add(RootDestination.Metrics)
                        }
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Workouts") },
                    selected = selectedDestination == RootDestination.Workouts,
                    onClick = {
                        if (selectedDestination != RootDestination.Workouts) {
                            rootBackStack.clear()
                            rootBackStack.add(RootDestination.Workouts)
                        }
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.FitnessCenter, contentDescription = null) },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    ) {
        NavDisplay(
            backStack = rootBackStack,
            onBack = {
                if (rootBackStack.size > 1) {
                    rootBackStack.removeLastOrNull()
                }
            },
            entryProvider = { key ->
                when (key) {
                    RootDestination.Metrics -> NavEntry(key) { metricsEntry(openGlobalSwitcher) }
                    RootDestination.Workouts -> NavEntry(key) { workoutsEntry(openGlobalSwitcher) }
                    else -> NavEntry(key) { metricsEntry(openGlobalSwitcher) }
                }
            }
        )
    }
}
