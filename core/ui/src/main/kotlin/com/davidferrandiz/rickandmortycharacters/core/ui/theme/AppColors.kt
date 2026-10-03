package com.davidferrandiz.rickandmortycharacters.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class AppColors(
    val divider: Color,
    val chipSelectedContainer: Color,
    val chipSelectedContent: Color,
    val statusAlive: Color,
    val statusDead: Color,
    val statusUnknown: Color,
    val offlineBarContainer: Color,
    val offlineBarContent: Color,
    val offlineBarSecondaryContent: Color,
    val offlineBarAction: Color,
    val skeletonBase: Color,
    val skeletonHighlight: Color,
    val sheetHandle: Color,
    val emptyIconBorder: Color,
    val imagePlaceholder: Color,
)

internal val LightAppColors = AppColors(
    divider = Color(0xFFECEDE8),
    chipSelectedContainer = Ink,
    chipSelectedContent = Color(0xFFFFFFFF),
    statusAlive = Color(0xFF2E9E4F),
    statusDead = Color(0xFFC2331F),
    statusUnknown = Color(0xFF8A8F8B),
    offlineBarContainer = Ink,
    offlineBarContent = Color(0xFFFFFFFF),
    offlineBarSecondaryContent = Color(0xFFC9CCC5),
    offlineBarAction = Lime,
    skeletonBase = Color(0xFFECEDE8),
    skeletonHighlight = Color(0xFFF6F6F3),
    sheetHandle = Color(0xFFD5D7D0),
    emptyIconBorder = Color(0xFFB9BCB4),
    imagePlaceholder = Color(0xFFECEDE8),
)

internal val DarkAppColors = AppColors(
    divider = Color(0xFF242826),
    chipSelectedContainer = Color(0xFFF2F3EF),
    chipSelectedContent = Ink,
    statusAlive = Color(0xFF4CC76F),
    statusDead = Color(0xFFF0705C),
    statusUnknown = Color(0xFF9AA09A),
    offlineBarContainer = Color(0xFF2A2E2B),
    offlineBarContent = Color(0xFFF2F3EF),
    offlineBarSecondaryContent = Color(0xFFC9CCC5),
    offlineBarAction = Lime,
    skeletonBase = Color(0xFF1F2321),
    skeletonHighlight = Color(0xFF2A2E2B),
    sheetHandle = Color(0xFF3A3F3B),
    emptyIconBorder = Color(0xFF3A3F3B),
    imagePlaceholder = Color(0xFF1F2321),
)

internal val LocalAppColors = staticCompositionLocalOf { LightAppColors }
