package com.davidferrandiz.rickandmortycharacters.core.ui.transition

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

const val TRANSITION_DURATION_MILLIS = 360
val TransitionEasing = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f)

private val BODY_SLIDE_DISTANCE = 28.dp

fun <T> transitionSpec(): FiniteAnimationSpec<T> = tween(TRANSITION_DURATION_MILLIS, easing = TransitionEasing)

val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }
val LocalAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.characterImageSharedElement(
    characterId: Int,
    cornerRadius: Dp,
    counterpartCornerRadius: Dp,
): Modifier {
    val sharedTransitionScope = LocalSharedTransitionScope.current ?: return this
    val animatedVisibilityScope = LocalAnimatedVisibilityScope.current ?: return this
    val animatedRadius = animatedVisibilityScope.transition.animateDp(
        transitionSpec = { transitionSpec() },
        label = "characterImageCorner",
    ) { state -> if (state == EnterExitState.Visible) cornerRadius else counterpartCornerRadius }
    val overlayClip = remember(animatedRadius) { TopCornersOverlayClip(animatedRadius) }
    return with(sharedTransitionScope) {
        sharedElement(
            sharedContentState = rememberSharedContentState(key = "character-image-$characterId"),
            animatedVisibilityScope = animatedVisibilityScope,
            boundsTransform = { _, _ -> transitionSpec() },
            clipInOverlayDuringTransition = overlayClip,
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
private class TopCornersOverlayClip(
    private val radius: State<Dp>,
) : SharedTransitionScope.OverlayClip {

    private val path = Path()

    override fun getClipPath(
        sharedContentState: SharedTransitionScope.SharedContentState,
        bounds: Rect,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Path {
        val corner = CornerRadius(with(density) { radius.value.toPx() })
        path.rewind()
        path.addRoundRect(RoundRect(rect = bounds, topLeft = corner, topRight = corner))
        return path
    }
}

@Composable
fun Modifier.slideUpWithScreen(): Modifier {
    val animatedVisibilityScope = LocalAnimatedVisibilityScope.current ?: return this
    val distance = with(LocalDensity.current) { BODY_SLIDE_DISTANCE.roundToPx() }
    return with(animatedVisibilityScope) {
        animateEnterExit(
            enter = slideInVertically(transitionSpec()) { distance },
            exit = slideOutVertically(transitionSpec()) { distance },
        )
    }
}
