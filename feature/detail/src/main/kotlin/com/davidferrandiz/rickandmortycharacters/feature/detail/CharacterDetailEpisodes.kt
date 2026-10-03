package com.davidferrandiz.rickandmortycharacters.feature.detail

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.davidferrandiz.rickandmortycharacters.core.ui.component.TextAction
import com.davidferrandiz.rickandmortycharacters.core.ui.component.rememberShimmerProgress
import com.davidferrandiz.rickandmortycharacters.core.ui.component.shimmer
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.AppShapes
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.AppTheme
import com.davidferrandiz.rickandmortycharacters.domain.model.Episode
import kotlinx.coroutines.launch
import com.davidferrandiz.rickandmortycharacters.core.ui.R as CoreUiR

private const val COLLAPSED_EPISODES = 3
private val ROW_MIN_HEIGHT = 52.dp

@Composable
internal fun DetailEpisodes(
    episodeCount: Int,
    episodes: EpisodesUiState,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val scope = rememberCoroutineScope()
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.bringIntoViewRequester(bringIntoViewRequester),
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(R.string.detail_episodes),
                style = AppTheme.typography.sectionTitle,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                text = pluralStringResource(R.plurals.detail_episode_count, episodeCount, episodeCount),
                style = AppTheme.typography.codeLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, AppShapes.Panel)
                .animateContentSize()
                .padding(horizontal = 16.dp, vertical = 4.dp),
        ) {
            when (episodes) {
                EpisodesUiState.Loading -> EpisodesLoading()
                is EpisodesUiState.Error -> EpisodesError(onRetry)
                is EpisodesUiState.Content -> EpisodesList(
                    episodes = episodes.episodes,
                    expanded = expanded,
                    onToggle = {
                        onExpandedChange(!expanded)
                        if (expanded) scope.launch { bringIntoViewRequester.bringIntoView() }
                    },
                )
            }
        }
    }
}

@Composable
private fun EpisodesList(episodes: List<Episode>, expanded: Boolean, onToggle: () -> Unit) {
    val canExpand = episodes.size > COLLAPSED_EPISODES
    val visible = if (expanded) episodes else episodes.take(COLLAPSED_EPISODES)
    visible.forEachIndexed { index, episode ->
        EpisodeRow(episode)
        if (canExpand || index < visible.lastIndex) Divider()
    }
    if (canExpand) {
        EpisodesToggle(total = episodes.size, expanded = expanded, onClick = onToggle)
    }
}

@Composable
private fun EpisodeRow(episode: Episode) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = ROW_MIN_HEIGHT)
            .padding(vertical = 8.dp),
    ) {
        Text(
            text = episode.code,
            style = AppTheme.typography.code,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = episode.name,
            style = AppTheme.typography.rowValue,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun EpisodesToggle(total: Int, expanded: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .height(ROW_MIN_HEIGHT)
            .clip(RoundedCornerShape(8.dp))
            .clickable(role = Role.Button, onClick = onClick),
    ) {
        Text(
            text = if (expanded) {
                stringResource(R.string.detail_show_less)
            } else {
                pluralStringResource(R.plurals.detail_see_all_episodes, total, total)
            },
            style = AppTheme.typography.buttonSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Icon(
            painter = painterResource(R.drawable.ic_chevron_down),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .size(20.dp)
                .rotate(if (expanded) 180f else 0f),
        )
    }
}

@Composable
private fun EpisodesLoading() {
    val progress = rememberShimmerProgress()
    val description = stringResource(R.string.detail_episodes_loading)
    Column(modifier = Modifier.clearAndSetSemantics { contentDescription = description }) {
        repeat(COLLAPSED_EPISODES) { index ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ROW_MIN_HEIGHT),
            ) {
                SkeletonBar(progress, Modifier.width(48.dp))
                SkeletonBar(progress, Modifier.fillMaxWidth(0.6f))
            }
            if (index < COLLAPSED_EPISODES - 1) Divider()
        }
    }
}

@Composable
private fun SkeletonBar(progress: State<Float>, modifier: Modifier = Modifier) {
    Box(
        modifier
            .height(12.dp)
            .clip(RoundedCornerShape(6.dp))
            .shimmer(progress, AppTheme.colors.skeletonBase, AppTheme.colors.skeletonHighlight),
    )
}

@Composable
private fun EpisodesError(onRetry: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = ROW_MIN_HEIGHT),
    ) {
        Text(
            text = stringResource(R.string.detail_episodes_error),
            style = AppTheme.typography.meta,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        TextAction(
            text = stringResource(CoreUiR.string.action_retry),
            onClick = onRetry,
            underlined = true,
        )
    }
}

@Composable
private fun Divider() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(AppTheme.colors.divider),
    )
}
