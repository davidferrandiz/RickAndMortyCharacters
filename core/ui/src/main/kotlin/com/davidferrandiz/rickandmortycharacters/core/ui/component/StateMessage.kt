package com.davidferrandiz.rickandmortycharacters.core.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.davidferrandiz.rickandmortycharacters.core.ui.R
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.AppTheme
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.RickAndMortyTheme

@Composable
fun StateMessage(
    @DrawableRes icon: Int,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    dashedIconBorder: Boolean = false,
    action: @Composable () -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 32.dp, end = 32.dp, top = 24.dp, bottom = 96.dp)
            .semantics { liveRegion = LiveRegionMode.Polite },
    ) {
        StateIcon(icon = icon, dashed = dashedIconBorder)
        Text(
            text = title,
            style = AppTheme.typography.title,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = body,
            style = AppTheme.typography.body,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Box(modifier = Modifier.padding(top = 8.dp)) {
            action()
        }
    }
}

@Composable
private fun StateIcon(@DrawableRes icon: Int, dashed: Boolean) {
    val borderColor = if (dashed) AppTheme.colors.emptyIconBorder else MaterialTheme.colorScheme.outline
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(88.dp)
            .background(MaterialTheme.colorScheme.surface, CircleShape)
            .drawBehind { drawCircleBorder(borderColor, dashed) },
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(32.dp),
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCircleBorder(color: Color, dashed: Boolean) {
    val strokeWidth = 1.dp.toPx()
    val dash = 4.dp.toPx()
    drawCircle(
        color = color,
        radius = (size.minDimension - strokeWidth) / 2,
        style = Stroke(
            width = strokeWidth,
            pathEffect = if (dashed) PathEffect.dashPathEffect(floatArrayOf(dash, dash)) else null,
        ),
    )
}

@PreviewLightDark
@Composable
private fun StateMessagePreview() {
    RickAndMortyTheme {
        StateMessage(
            icon = R.drawable.ic_search_off,
            title = "No characters named “zara”",
            body = "Check the spelling or try another name. Not in this dimension, at least.",
            dashedIconBorder = true,
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
        ) {
            SecondaryButton(text = "Clear search and filters", onClick = {})
        }
    }
}
