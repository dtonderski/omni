@file:OptIn(ExperimentalMaterial3Api::class)

package com.omni

import android.R.attr.translationY
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omni.core.ui.theme.OmniTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OmniTheme {
                MainShell()
            }
        }
    }
}
@Composable
fun MainShell() {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    // STATE: Height of the bar (captured via onGloballyPositioned)
    var topBarHeightPx by remember { mutableFloatStateOf(0f) }

    // ANIMATABLE: Replaces the simple float state so we can animate snaps
    val topBarOffset = remember { Animatable(0f) }

    val scope = rememberCoroutineScope() // Needed to launch snap animations

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {

            // 1. DRAGGING LOGIC (Same as before, but using .snapTo)
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // Don't move if we haven't measured height yet
                if (topBarHeightPx == 0f) return Offset.Zero

                val delta = available.y
                val newOffset = topBarOffset.value + delta

                // We use runBlocking or launch for snapTo because it's technically a suspend function,
                // but since we are in the UI thread loop of PreScroll, we can just launch it.
                // Note: Using snapTo is instant, no animation.
                scope.launch {
                    topBarOffset.snapTo(newOffset.coerceIn(-topBarHeightPx, 0f))
                }

                return Offset.Zero
            }

            // 2. SNAPPING LOGIC (The new part)
            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                if (topBarHeightPx == 0f) return super.onPostFling(consumed, available)

                val currentOffset = topBarOffset.value
                val velocity = available.y

                // Logic: Where should we go?
                val targetOffset = when {
                    // Case A: Hard Fling UP -> Hide
                    velocity < -500 -> -topBarHeightPx
                    // Case B: Hard Fling DOWN -> Show
                    velocity > 500 -> 0f
                    // Case C: Stopped / Slow Drag -> Snap to nearest neighbor
                    // If we are more than halfway hidden, hide fully. Otherwise show.
                    currentOffset < -(topBarHeightPx / 2) -> -topBarHeightPx
                    else -> 0f
                }

                // Launch the snap animation
                scope.launch {
                    topBarOffset.animateTo(
                        targetValue = targetOffset,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow) // Bouncy snap
                    )
                }

                return super.onPostFling(consumed, available)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        // 1. CONTENT
        MetricList(modifier = Modifier.fillMaxSize())

        // 2. TOP BAR
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .onGloballyPositioned { topBarHeightPx = it.size.height.toFloat() }
                // Use the Animatable's value
                .offset { IntOffset(x = 0, y = topBarOffset.value.roundToInt()) }
        ) {
            Box(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                DockedSearchBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, CircleShape),
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { isSearchActive = false },
                    active = isSearchActive,
                    onActiveChange = { active ->
                        isSearchActive = active
                        if (active) scope.launch { topBarOffset.animateTo(0f) }
                    },
                    placeholder = { Text("Search Metrics") },
                    shape = CircleShape,
                    colors = SearchBarDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.98f)
                    ),
                    leadingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OmniLogo(onClick = { /* Switch Feature */ })
                            VerticalDivider(modifier = Modifier.height(24.dp).padding(horizontal = 8.dp))
                        }
                    },
                    trailingIcon = { Icon(Icons.Default.Search, null, Modifier.padding(end = 8.dp)) }
                ) {
                    Text("Results...", Modifier.padding(16.dp))
                }
            }
        }

        // 3. BOTTOM BAR
        Box(modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding()) {
            FloatingBottomNav()
        }
    }
}

@Composable
fun OmniLogo(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .size(42.dp)
            .padding(4.dp),
        shadowElevation = 4.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.FilterCenterFocus,
                contentDescription = "Logo",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun MetricList(modifier: Modifier = Modifier) {
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

    LazyColumn(
        modifier = modifier,
        // TOP PADDING: 100dp ensures first card starts below the search pill
        // BOTTOM PADDING: 120dp ensures last card isn't covered by bottom nav
        contentPadding = PaddingValues(top = 100.dp, bottom = 140.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(dummyMetrics) { metric ->
            PremiumMetricCard(metric)
        }
    }
}

@Composable
fun PremiumMetricCard(data: MetricData) {
    Surface(
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = data.label.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = data.value,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = data.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = data.icon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxSize(),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun FloatingBottomNav() {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.96f),
        shadowElevation = 12.dp,
        tonalElevation = 8.dp,
        modifier = Modifier
            .padding(bottom = 24.dp)
            .height(64.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NavPill("Explore", Icons.Default.Explore, active = true)
            NavPill("Saved", Icons.Default.Bookmark, active = false)
            VerticalDivider(
                modifier = Modifier
                    .height(24.dp)
                    .padding(horizontal = 4.dp)
            )
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

@Composable
fun NavPill(label: String, icon: ImageVector, active: Boolean) {
    val container =
        if (active) MaterialTheme.colorScheme.primaryContainer else androidx.compose.ui.graphics.Color.Transparent
    val content =
        if (active) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    Surface(shape = CircleShape, color = container, modifier = Modifier.height(48.dp)) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, null, modifier = Modifier.size(20.dp), tint = content)
            Text(label, style = MaterialTheme.typography.labelLarge, color = content)
        }
    }
}

data class MetricData(
    val label: String,
    val value: String,
    val subtitle: String,
    val icon: ImageVector
)