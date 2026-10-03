package com.davidferrandiz.rickandmortycharacters.feature.characters

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.AppShapes
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.AppTheme

@Composable
internal fun FilterButton(
    activeFilters: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val description = if (activeFilters > 0) {
        pluralStringResource(R.plurals.filters_open_active, activeFilters, activeFilters)
    } else {
        stringResource(R.string.filters_open)
    }
    Box(modifier = modifier.size(52.dp)) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .matchParentSize()
                .clip(AppShapes.Field)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, AppShapes.Field)
                .clickable(role = Role.Button, onClick = onClick)
                .semantics { contentDescription = description },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_filter),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(22.dp),
            )
        }
        if (activeFilters > 0) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 6.dp, y = (-6).dp)
                    .defaultMinSize(minWidth = 22.dp)
                    .height(22.dp)
                    .background(MaterialTheme.colorScheme.primary, AppShapes.Pill)
                    .padding(horizontal = 6.dp)
                    .clearAndSetSemantics { },
            ) {
                Text(
                    text = activeFilters.toString(),
                    style = AppTheme.typography.badge,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}
