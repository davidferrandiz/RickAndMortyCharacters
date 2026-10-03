package com.davidferrandiz.rickandmortycharacters.feature.characters

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.davidferrandiz.rickandmortycharacters.core.ui.component.PrimaryButton
import com.davidferrandiz.rickandmortycharacters.core.ui.component.SecondaryButton
import com.davidferrandiz.rickandmortycharacters.core.ui.component.SkeletonCard
import com.davidferrandiz.rickandmortycharacters.core.ui.component.StateMessage
import com.davidferrandiz.rickandmortycharacters.core.ui.component.TextAction
import com.davidferrandiz.rickandmortycharacters.core.ui.component.labelRes
import com.davidferrandiz.rickandmortycharacters.core.ui.component.messageRes
import com.davidferrandiz.rickandmortycharacters.core.ui.component.rememberShimmerProgress
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.AppShapes
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.AppTheme
import com.davidferrandiz.rickandmortycharacters.domain.error.AppError
import com.davidferrandiz.rickandmortycharacters.core.ui.R as CoreUiR

private const val SKELETON_CARD_COUNT = 6

@Composable
internal fun CharactersSkeleton(contentPadding: PaddingValues, modifier: Modifier = Modifier) {
    val progress = rememberShimmerProgress()
    val description = stringResource(R.string.characters_loading)
    LazyVerticalGrid(
        columns = GridCells.Adaptive(MIN_CARD_WIDTH),
        horizontalArrangement = Arrangement.spacedBy(GRID_SPACING),
        verticalArrangement = Arrangement.spacedBy(GRID_SPACING),
        contentPadding = contentPadding,
        userScrollEnabled = false,
        modifier = modifier.clearAndSetSemantics { contentDescription = description },
    ) {
        items(SKELETON_CARD_COUNT) {
            SkeletonCard(progress)
        }
    }
}

@Composable
internal fun CharactersEmpty(query: String, onClearFilters: () -> Unit, modifier: Modifier = Modifier) {
    val trimmedQuery = query.trim()
    StateMessage(
        icon = CoreUiR.drawable.ic_search_off,
        title = if (trimmedQuery.isEmpty()) {
            stringResource(R.string.empty_title)
        } else {
            stringResource(R.string.empty_title_query, trimmedQuery)
        },
        body = stringResource(if (trimmedQuery.isEmpty()) R.string.empty_body else R.string.empty_body_query),
        dashedIconBorder = true,
        modifier = modifier,
    ) {
        SecondaryButton(text = stringResource(R.string.action_clear_filters), onClick = onClearFilters)
    }
}

@Composable
internal fun CharactersError(error: AppError, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    StateMessage(
        icon = CoreUiR.drawable.ic_offline,
        title = stringResource(R.string.error_title),
        body = stringResource(error.messageRes),
        modifier = modifier,
    ) {
        PrimaryButton(text = stringResource(CoreUiR.string.action_try_again), onClick = onRetry)
    }
}

@Composable
internal fun OfflineBar(error: AppError, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .background(AppTheme.colors.offlineBarContainer, AppShapes.Card)
            .padding(start = 16.dp, top = 10.dp, end = 8.dp, bottom = 10.dp)
            .semantics { liveRegion = LiveRegionMode.Polite },
    ) {
        Icon(
            painter = painterResource(CoreUiR.drawable.ic_offline),
            contentDescription = null,
            tint = AppTheme.colors.offlineBarContent,
            modifier = Modifier.size(22.dp),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = stringResource(error.labelRes),
                style = AppTheme.typography.buttonSmall,
                color = AppTheme.colors.offlineBarContent,
            )
            Text(
                text = stringResource(R.string.offline_subtitle),
                style = AppTheme.typography.meta,
                color = AppTheme.colors.offlineBarSecondaryContent,
            )
        }
        TextAction(
            text = stringResource(CoreUiR.string.action_retry),
            onClick = onRetry,
            color = AppTheme.colors.offlineBarAction,
        )
    }
}

@Composable
internal fun LoadingMoreFooter(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 4.dp),
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.onSurface,
            trackColor = AppTheme.colors.sheetHandle,
            strokeWidth = 2.dp,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = stringResource(R.string.loading_more),
            style = AppTheme.typography.meta,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
internal fun LoadMoreErrorFooter(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        modifier = modifier
            .fillMaxWidth()
            .semantics { liveRegion = LiveRegionMode.Polite },
    ) {
        Text(
            text = stringResource(R.string.load_more_error),
            style = AppTheme.typography.meta,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        TextAction(
            text = stringResource(CoreUiR.string.action_retry),
            onClick = onRetry,
            underlined = true,
        )
    }
}
