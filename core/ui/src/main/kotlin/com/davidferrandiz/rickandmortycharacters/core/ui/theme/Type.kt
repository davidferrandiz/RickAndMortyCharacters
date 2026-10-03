package com.davidferrandiz.rickandmortycharacters.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.davidferrandiz.rickandmortycharacters.core.ui.R

private fun variableFont(resId: Int, weight: FontWeight) = Font(
    resId = resId,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
)

internal val BricolageGrotesque = FontFamily(
    variableFont(R.font.bricolage_grotesque, FontWeight.SemiBold),
    variableFont(R.font.bricolage_grotesque, FontWeight.Bold),
)

internal val InstrumentSans = FontFamily(
    variableFont(R.font.instrument_sans, FontWeight.Normal),
    variableFont(R.font.instrument_sans, FontWeight.Medium),
    variableFont(R.font.instrument_sans, FontWeight.SemiBold),
)

internal val DmMono = FontFamily(
    Font(R.font.dm_mono_regular, FontWeight.Normal),
    Font(R.font.dm_mono_medium, FontWeight.Medium),
)

@Immutable
data class AppTypography(
    val screenTitle: TextStyle,
    val detailName: TextStyle,
    val title: TextStyle,
    val sectionTitle: TextStyle,
    val cardName: TextStyle,
    val body: TextStyle,
    val input: TextStyle,
    val button: TextStyle,
    val buttonSmall: TextStyle,
    val rowValue: TextStyle,
    val chip: TextStyle,
    val meta: TextStyle,
    val factValue: TextStyle,
    val eyebrow: TextStyle,
    val factLabel: TextStyle,
    val code: TextStyle,
    val codeLarge: TextStyle,
    val badge: TextStyle,
)

internal val DefaultAppTypography = AppTypography(
    screenTitle = TextStyle(
        fontFamily = BricolageGrotesque,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 1.05.em,
        letterSpacing = (-0.02).em,
    ),
    detailName = TextStyle(
        fontFamily = BricolageGrotesque,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 1.05.em,
        letterSpacing = (-0.02).em,
    ),
    title = TextStyle(
        fontFamily = BricolageGrotesque,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 1.15.em,
    ),
    sectionTitle = TextStyle(
        fontFamily = BricolageGrotesque,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
    ),
    cardName = TextStyle(
        fontFamily = BricolageGrotesque,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 1.2.em,
    ),
    body = TextStyle(
        fontFamily = InstrumentSans,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 1.45.em,
    ),
    input = TextStyle(
        fontFamily = InstrumentSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    ),
    button = TextStyle(
        fontFamily = InstrumentSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
    ),
    buttonSmall = TextStyle(
        fontFamily = InstrumentSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
    ),
    rowValue = TextStyle(
        fontFamily = InstrumentSans,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
    ),
    chip = TextStyle(
        fontFamily = InstrumentSans,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
    ),
    meta = TextStyle(
        fontFamily = InstrumentSans,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
    ),
    factValue = TextStyle(
        fontFamily = InstrumentSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
    ),
    eyebrow = TextStyle(
        fontFamily = DmMono,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.06.em,
    ),
    factLabel = TextStyle(
        fontFamily = DmMono,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        letterSpacing = 0.06.em,
    ),
    code = TextStyle(
        fontFamily = DmMono,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
    ),
    codeLarge = TextStyle(
        fontFamily = DmMono,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
    ),
    badge = TextStyle(
        fontFamily = DmMono,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
    ),
)

internal val LocalAppTypography = staticCompositionLocalOf { DefaultAppTypography }

internal val MaterialTypography = Typography().run {
    copy(
        displayLarge = displayLarge.copy(fontFamily = InstrumentSans),
        displayMedium = displayMedium.copy(fontFamily = InstrumentSans),
        displaySmall = displaySmall.copy(fontFamily = InstrumentSans),
        headlineLarge = headlineLarge.copy(fontFamily = InstrumentSans),
        headlineMedium = headlineMedium.copy(fontFamily = InstrumentSans),
        headlineSmall = headlineSmall.copy(fontFamily = InstrumentSans),
        titleLarge = titleLarge.copy(fontFamily = InstrumentSans),
        titleMedium = titleMedium.copy(fontFamily = InstrumentSans),
        titleSmall = titleSmall.copy(fontFamily = InstrumentSans),
        bodyLarge = bodyLarge.copy(fontFamily = InstrumentSans),
        bodyMedium = bodyMedium.copy(fontFamily = InstrumentSans),
        bodySmall = bodySmall.copy(fontFamily = InstrumentSans),
        labelLarge = labelLarge.copy(fontFamily = InstrumentSans),
        labelMedium = labelMedium.copy(fontFamily = InstrumentSans),
        labelSmall = labelSmall.copy(fontFamily = InstrumentSans),
    )
}
