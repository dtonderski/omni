package com.omni.metrics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PremiumMetricCard(data: MetricCardData) {
    val isLight = !isSystemInDarkTheme()
    val palette = if (isLight) {
        tierPaletteLight(data.tierLabel)
    } else {
        tierPaletteDark(data.tierLabel)
    }
    val contentColor = palette?.content ?: data.accentStart
    val barFillStart = palette?.barStart ?: data.accentStart
    val barFillEnd = palette?.barEnd ?: data.accentEnd
    val tierChipContainer = palette?.container
    val tierChipContent = palette?.content
    val nextTierColor =
        if (isLight) tierPaletteLight(data.nextTierLabel)?.content else tierPaletteDark(data.nextTierLabel)?.content
    val cardColor = MaterialTheme.colorScheme.surfaceContainerLow
    val emptyBarColor = (data.tierGradientStart ?: data.accentStart).copy(alpha = 0.2f)
    val powerLineOuterWidth = 10.dp
    val powerLineInnerWidth = 6.dp
    val hasObjective =
        !data.objectivePeriodLabel.isNullOrBlank() ||
                !data.objectiveAggregationLabel.isNullOrBlank() ||
                !data.tierLabel.isNullOrBlank() ||
                data.progress != null ||
                data.progressSegments != null
    val borderColor = contentColor.copy(alpha = 0.5f)
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = cardColor,
        tonalElevation = 1.dp,
        shadowElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .then(
                Modifier.border(1.dp, borderColor, RoundedCornerShape(28.dp))
            )
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = if (hasObjective) 20.dp else 14.dp)
                    .padding(end = 52.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .width(powerLineOuterWidth)
                        .height(120.dp)
                ) {

                    if (hasObjective) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(powerLineInnerWidth)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(barFillStart, barFillEnd)
                                    )
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(if (hasObjective) 14.dp else 6.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = data.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh
                        ) {
                            Text(
                                text = data.logResolutionLabel,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = data.value,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        val metaParts = buildList {
                            if (!data.unit.isNullOrBlank()) add(data.unit)
                            if (data.displayPeriodLabel.equals(
                                    data.displayAggregationLabel,
                                    ignoreCase = true
                                )
                            ) {
                                add(data.displayPeriodLabel)
                            } else {
                                add(data.displayPeriodLabel)
                                add(data.displayAggregationLabel)
                            }
                        }
                        val metaText = metaParts.joinToString(" • ")
                        Text(
                            text = metaText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (!data.tierLabel.isNullOrBlank() && !data.nextMilestoneLabel.isNullOrBlank()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val tierStart =
                                tierChipContent ?: data.tierGradientStart ?: data.accentStart
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = tierChipContainer ?: tierStart.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = data.tierLabel,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = tierStart,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                            if (data.nextTierLabel != null && nextTierColor != null) {
                                Text(
                                    text = buildAnnotatedString {
                                        append("Next: ")
                                        withStyle(SpanStyle(color = nextTierColor)) {
                                            append(data.nextTierLabel)
                                        }
                                        if (!data.nextTierValue.isNullOrBlank()) {
                                            if (!data.unit.isNullOrBlank()) {
                                                append(" (${data.nextTierValue} ${data.unit})")
                                            } else {
                                                append(" (${data.nextTierValue})")
                                            }
                                        }
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Text(
                                    text = data.nextMilestoneLabel,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    when {
                        data.progressSegments != null && data.segmentWeights != null -> {
                            SegmentedProgressBarWeighted(
                                segmentFills = data.progressSegments,
                                segmentWeights = data.segmentWeights,
                                fillStart = barFillStart,
                                fillEnd = barFillEnd,
                                emptyColor = emptyBarColor,
                                useGradient = data.useGradient
                            )
                        }

                        data.progress != null -> {
                            SegmentedProgressBar(
                                progress = data.progress,
                                segments = 4,
                                fillStart = barFillStart,
                                fillEnd = barFillEnd,
                                emptyColor = emptyBarColor,
                                useGradient = data.useGradient
                            )
                        }
                    }

                    if (!data.objectivePeriodLabel.isNullOrBlank() && !data.objectiveAggregationLabel.isNullOrBlank()) {
                        Text(
                            text = "${data.objectivePeriodLabel} • ${data.objectiveAggregationLabel}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.4.sp
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .size(48.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .border(
                        width = 1.dp,
                        color = contentColor.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .background(
                        SolidColor(Color.Transparent)
                    )
                    .align(Alignment.TopEnd), contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = data.icon,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = contentColor
                )
            }
        }
    }
}

@Composable
private fun SegmentedProgressBar(
    progress: Float,
    segments: Int,
    fillStart: Color,
    fillEnd: Color,
    emptyColor: Color,
    useGradient: Boolean
) {
    SegmentedProgressBarWeighted(
        segmentFills = List(segments) { index ->
            val start = index / segments.toFloat()
            val end = (index + 1) / segments.toFloat()
            ((progress.coerceIn(0f, 1f) - start) / (end - start)).coerceIn(0f, 1f)
        },
        segmentWeights = List(segments) { 1f },
        fillStart = fillStart,
        fillEnd = fillEnd,
        emptyColor = emptyColor,
        useGradient = useGradient
    )
}

@Composable
private fun SegmentedProgressBarWeighted(
    segmentFills: List<Float>,
    segmentWeights: List<Float>,
    fillStart: Color,
    fillEnd: Color,
    emptyColor: Color,
    useGradient: Boolean
) {
    val safeWeights =
        segmentWeights.ifEmpty { List(segmentFills.size) { 1f } }
    val gap = 4.dp
    val gapPx = with(androidx.compose.ui.platform.LocalDensity.current) { gap.toPx() }
    val radius = with(androidx.compose.ui.platform.LocalDensity.current) { 999.dp.toPx() }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
    ) {
        drawSegmentedBar(
            segmentFills = segmentFills,
            segmentWeights = safeWeights,
            fillStart = fillStart,
            fillEnd = fillEnd,
            emptyColor = emptyColor,
            useGradient = useGradient,
            gapPx = gapPx,
            radius = radius
        )
    }
}

private fun DrawScope.drawSegmentedBar(
    segmentFills: List<Float>,
    segmentWeights: List<Float>,
    fillStart: Color,
    fillEnd: Color,
    emptyColor: Color,
    useGradient: Boolean,
    gapPx: Float,
    radius: Float
) {
    val totalWeight = segmentWeights.sum().coerceAtLeast(0.1f)
    val gaps = gapPx * (segmentFills.size - 1).coerceAtLeast(0)
    val availableWidth = (size.width - gaps).coerceAtLeast(0f)
    var x = 0f
    val brush = if (useGradient) {
        Brush.horizontalGradient(
            colors = listOf(fillStart, fillEnd), startX = 0f, endX = size.width
        )
    } else {
        null
    }
    segmentFills.forEachIndexed { index, fill ->
        val weight = segmentWeights.getOrElse(index) { 1f }.coerceAtLeast(0.1f)
        val width = availableWidth * (weight / totalWeight)
        val rectSize = Size(width, size.height)
        drawRoundRect(
            color = emptyColor,
            topLeft = Offset(x, 0f),
            size = rectSize,
            cornerRadius = CornerRadius(radius, radius)
        )
        if (fill > 0f) {
            val fillSize = Size(width * fill.coerceIn(0f, 1f), size.height)
            if (brush != null) {
                drawRoundRect(
                    brush = brush,
                    topLeft = Offset(x, 0f),
                    size = fillSize,
                    cornerRadius = CornerRadius(radius, radius)
                )
            } else {
                drawRoundRect(
                    color = fillStart,
                    topLeft = Offset(x, 0f),
                    size = fillSize,
                    cornerRadius = CornerRadius(radius, radius)
                )
            }
        }
        x += width + gapPx
    }
}

private data class TierPalette(
    val content: Color,
    val container: Color,
    val barStart: Color,
    val barEnd: Color
)

private fun tierPaletteLight(label: String?): TierPalette? {
    return when (label?.lowercase()) {
        "bronze" -> TierPalette(
            content = Color(0xFF9C5536),
            container = Color(0xFFFBECE7),
            barStart = Color(0xFF9C5536),
            barEnd = Color(0xFFC98066)
        )

        "silver" -> TierPalette(
            content = Color(0xFF4B5F73),
            container = Color(0xFFE2E8F0),
            barStart = Color(0xFF94A3B8),
            barEnd = Color(0xFFCBD5E1)
        )

        "gold" -> TierPalette(
            content = Color(0xFFCA8A04),
            container = Color(0xFFFEFCE8),
            barStart = Color(0xFFEAB308),
            barEnd = Color(0xFFFACC15)
        )

        "diamond" -> TierPalette(
            // WAS: Color(0xFF58C7E2) -> Too light!
            // FIX: Color(0xFF007796) -> Deep Cyan (Readable & Premium)
            content = Color(0xFF007796),

            container = Color(0xFFE1F5FE),
            barStart = Color(0xFF0284C7),
            barEnd = Color(0xFF5BC0DE)
        )

        else -> null
    }
}

private fun tierPaletteDark(label: String?): TierPalette? {
    return when (label?.lowercase()) {
        "bronze" -> TierPalette(
            // Text: Warmer, more vibrant orange (less "flesh tone")
            content = Color(0xFFFFB74D),
            // Chip BG: Very dark reddish-brown (high contrast vs text)
            container = Color(0xFF3E2723),
            // Bar: Kept your gradient logic
            barStart = Color(0xFFD56C4B),
            barEnd = Color(0xFFF0A48A)
        )

        "silver" -> TierPalette(
            // Text: Clean, crisp grey-blue (not murky grey)
            content = Color(0xFFE2E8F0),
            // Chip BG: Deep Slate (looks metallic, not disabled)
            container = Color(0xFF334155),
            // Bar: Kept your gradient logic
            barStart = Color(0xFF7F8E98),
            barEnd = Color(0xFFDDE3E8)
        )

        "gold" -> TierPalette(
            // Text: Vibrant "Cyberpunk Gold" (Yellow-Amber)
            content = Color(0xFFFFD54F),
            // Chip BG: Deep rich amber/brown (expensive feel)
            container = Color(0xFF422006),
            // Bar: Kept your gradient logic
            barStart = Color(0xFFB88300),
            barEnd = Color(0xFFFFD76A)
        )

        "diamond" -> TierPalette(
            // Text: Icy White (Not Cyan!) to cut through the dark
            content = Color(0xFF8AE1FF),
            // Chip BG: Very Dark "Petrol" Blue (Cold and Deep)
            container = Color(0xFF13303A),

            // Bar: Keep the gradient you liked
            barStart = Color(0xFF12848F),
            barEnd = Color(0xFF70EAFA)
        )

        else -> null
    }
}
