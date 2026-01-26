package com.omni.core.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun OmniScaffold(
    topBar: @Composable () -> Unit,
    bottomBar: @Composable () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    // 1. STATE
    var topBarHeightPx by remember { mutableFloatStateOf(0f) }
    val topBarOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    // 2. LOGIC
    val nestedScrollConnection = rememberCollapsingTopBarConnection(
        heightPx = topBarHeightPx,
        offsetAnimatable = topBarOffset,
        scope = scope
    )

    // 3. LAYOUT
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .nestedScroll(nestedScrollConnection)
    ) {
        // CONTENT SLOT
        // We pass padding so the feature knows where the safe zones are
        content(PaddingValues(top = 140.dp, bottom = 140.dp))

        // TOP BAR SLOT (Animated)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .onGloballyPositioned { topBarHeightPx = it.size.height.toFloat() }
                .offset { IntOffset(x = 0, y = topBarOffset.value.roundToInt()) }
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            topBar()
        }

        // BOTTOM BAR SLOT (Fixed)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 24.dp)
        ) {
            bottomBar()
        }
    }
}