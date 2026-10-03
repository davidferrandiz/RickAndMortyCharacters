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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
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
    val animatedRadius by animatedVisibilityScope.transition.animateDp(
        transitionSpec = { transitionSpec() },
        label = "characterImageCorner",
    ) { state -> if (state == EnterExitState.Visible) cornerRadius else counterpartCornerRadius }
    return with(sharedTransitionScope) {
        sharedElement(
            sharedContentState = rememberSharedContentState(key = "character-image-$characterId"),
            animatedVisibilityScope = animatedVisibilityScope,
            boundsTransform = { _, _ -> transitionSpec() },
            clipInOverlayDuringTransition = OverlayClip(
                RoundedCornerShape(topStart = animatedRadius, topEnd = animatedRadius),
            ),
        )
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
