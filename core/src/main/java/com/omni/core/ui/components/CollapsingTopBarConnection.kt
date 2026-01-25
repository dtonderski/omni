package com.omni.core.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Handles the physics for a top bar that hides on scroll and snaps
 * to a visible/hidden state on release.
 */
class CollapsingTopBarConnection(
    private val heightPx: Float,
    private val offsetAnimatable: Animatable<Float, AnimationVector1D>,
    private val scope: CoroutineScope
) : NestedScrollConnection {

    @Suppress("SameReturnValue")
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        // If we don't know the height yet, don't consume scroll
        if (heightPx == 0f) return Offset.Zero

        val delta = available.y
        val newOffset = offsetAnimatable.value + delta

        // Instant update while dragging
        scope.launch {
            offsetAnimatable.snapTo(newOffset.coerceIn(-heightPx, 0f))
        }

        // Return Zero so the content continues to scroll
        return Offset.Zero
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        if (heightPx == 0f) return super.onPostFling(consumed, available)

        val currentOffset = offsetAnimatable.value
        val velocity = available.y

        val targetOffset = when {
            // Fling UP -> Hide
            velocity < -VELOCITY_THRESHOLD -> -heightPx
            // Fling DOWN -> Show
            velocity > VELOCITY_THRESHOLD -> 0f
            // Dragged more than 50% up -> Hide
            currentOffset < -(heightPx / 2) -> -heightPx
            // Otherwise -> Show
            else -> 0f
        }

        // Animate the snap
        scope.launch {
            offsetAnimatable.animateTo(
                targetValue = targetOffset,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
            )
        }

        return super.onPostFling(consumed, available)
    }

    // Companion object
    companion object {
        private const val VELOCITY_THRESHOLD = 500
    }
}

// A Helper Composable to make using it easy
@Composable
fun rememberCollapsingTopBarConnection(
    heightPx: Float,
    offsetAnimatable: Animatable<Float, AnimationVector1D>,
    scope: CoroutineScope
): CollapsingTopBarConnection {
    return remember(heightPx, offsetAnimatable, scope) {
        CollapsingTopBarConnection(heightPx, offsetAnimatable, scope)
    }
}