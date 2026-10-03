package com.davidferrandiz.rickandmortycharacters.core.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

internal val Lime = Color(0xFFC6F432)
internal val Ink = Color(0xFF111312)

internal val LightColorScheme = lightColorScheme(
    primary = Lime,
    onPrimary = Ink,
    background = Color(0xFFF6F6F3),
    onBackground = Ink,
    surface = Color(0xFFFFFFFF),
    onSurface = Ink,
    surfaceContainerHigh = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFF5C615E),
    outline = Color(0xFFE3E4DF),
    outlineVariant = Color(0xFFE3E4DF),
    scrim = Color(0x73000000),
)

internal val DarkColorScheme = darkColorScheme(
    primary = Lime,
    onPrimary = Ink,
    background = Color(0xFF0E100F),
    onBackground = Color(0xFFF2F3EF),
    surface = Color(0xFF191C1A),
    onSurface = Color(0xFFF2F3EF),
    surfaceContainerHigh = Color(0xFF1F2321),
    onSurfaceVariant = Color(0xFFA3A8A2),
    outline = Color(0xFF2A2E2B),
    outlineVariant = Color(0xFF2A2E2B),
    scrim = Color(0x99000000),
)
