package com.davidferrandiz.rickandmortycharacters.feature.characters

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

@Composable
internal fun CollapsingHeader(
    header: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (headerHeight: Dp) -> Unit,
) {
    var headerHeightPx by remember { mutableIntStateOf(0) }
    var headerOffsetPx by remember { mutableFloatStateOf(0f) }
    val scrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                headerOffsetPx = (headerOffsetPx + available.y).coerceIn(-headerHeightPx.toFloat(), 0f)
                return Offset.Zero
            }
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .nestedScroll(scrollConnection),
    ) {
        content(with(LocalDensity.current) { headerHeightPx.toDp() })
        header(
            Modifier
                .onSizeChanged { headerHeightPx = it.height }
                .offset { IntOffset(x = 0, y = headerOffsetPx.roundToInt()) }
                .background(MaterialTheme.colorScheme.background),
        )
    }
}
