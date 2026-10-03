package com.davidferrandiz.rickandmortycharacters.core.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.AppShapes
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.AppTheme
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.RickAndMortyTheme

private const val SHIMMER_DURATION_MILLIS = 1_600

@Composable
fun rememberShimmerProgress(): State<Float> {
    val transition = rememberInfiniteTransition(label = "shimmer")
    return transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(SHIMMER_DURATION_MILLIS, easing = LinearEasing)),
        label = "shimmerProgress",
    )
}

fun Modifier.shimmer(progress: State<Float>, base: Color, highlight: Color): Modifier = drawBehind {
    val bandEnd = (progress.value * 3f - 1f) * size.width
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(base, highlight, base),
            start = Offset(bandEnd - size.width, 0f),
            end = Offset(bandEnd, 0f),
        ),
    )
}

@Composable
fun SkeletonCard(progress: State<Float>, modifier: Modifier = Modifier) {
    val base = AppTheme.colors.skeletonBase
    val highlight = AppTheme.colors.skeletonHighlight
    Column(
        modifier = modifier
            .clip(AppShapes.Card)
            .background(MaterialTheme.colorScheme.surface),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .shimmer(progress, base, highlight),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(start = 14.dp, top = 14.dp, end = 14.dp, bottom = 16.dp),
        ) {
            Box(
                Modifier
                    .fillMaxWidth(0.7f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .shimmer(progress, base, highlight),
            )
            Box(
                Modifier
                    .fillMaxWidth(0.52f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .shimmer(progress, base, highlight),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun SkeletonCardPreview() {
    RickAndMortyTheme {
        Box(
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
        ) {
            SkeletonCard(progress = rememberShimmerProgress(), modifier = Modifier.width(169.dp))
        }
    }
}
