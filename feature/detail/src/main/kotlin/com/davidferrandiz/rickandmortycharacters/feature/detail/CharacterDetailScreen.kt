package com.davidferrandiz.rickandmortycharacters.feature.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidferrandiz.rickandmortycharacters.core.ui.component.CharacterImage
import com.davidferrandiz.rickandmortycharacters.core.ui.component.PrimaryButton
import com.davidferrandiz.rickandmortycharacters.core.ui.component.StateMessage
import com.davidferrandiz.rickandmortycharacters.core.ui.component.messageRes
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.AppShapes
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.AppTheme
import com.davidferrandiz.rickandmortycharacters.domain.error.AppError
import com.davidferrandiz.rickandmortycharacters.domain.model.Character
import com.davidferrandiz.rickandmortycharacters.core.ui.R as CoreUiR

private val IMAGE_HEIGHT = 372.dp
private val BODY_OVERLAP = 28.dp

@Composable
fun CharacterDetailScreen(
    characterId: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CharacterDetailViewModel =
        hiltViewModel<CharacterDetailViewModel, CharacterDetailViewModel.Factory>(
            creationCallback = { factory -> factory.create(characterId) },
        ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CharacterDetailContent(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::onRetry,
        modifier = modifier,
    )
}

@Composable
internal fun CharacterDetailContent(
    uiState: CharacterDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        when (uiState) {
            CharacterDetailUiState.Loading -> Box(
                Modifier
                    .fillMaxWidth()
                    .height(IMAGE_HEIGHT)
                    .background(AppTheme.colors.imagePlaceholder),
            )
            is CharacterDetailUiState.Error -> DetailError(error = uiState.error, onRetry = onRetry)
            is CharacterDetailUiState.Content -> DetailBody(
                character = uiState.character,
                episodes = uiState.episodes,
                onRetry = onRetry,
            )
        }
        BackButton(
            onClick = onBack,
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 16.dp, top = 8.dp),
        )
    }
}

@Composable
private fun DetailBody(
    character: Character,
    episodes: EpisodesUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var episodesExpanded by rememberSaveable { mutableStateOf(false) }
    val navigationBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Box(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        CharacterImage(
            imageUrl = character.imageUrl,
            modifier = Modifier
                .fillMaxWidth()
                .height(IMAGE_HEIGHT),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .padding(top = IMAGE_HEIGHT - BODY_OVERLAP)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background, AppShapes.Sheet)
                .padding(start = 20.dp, top = 24.dp, end = 20.dp, bottom = 32.dp + navigationBarPadding),
        ) {
            DetailHeader(character)
            DetailFacts(character)
            DetailPlaces(character)
            DetailEpisodes(
                episodeCount = character.episodeIds.size,
                episodes = episodes,
                expanded = episodesExpanded,
                onExpandedChange = { episodesExpanded = it },
                onRetry = onRetry,
            )
        }
    }
}

@Composable
private fun DetailError(error: AppError, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    StateMessage(
        icon = CoreUiR.drawable.ic_offline,
        title = stringResource(R.string.detail_error_title),
        body = stringResource(error.messageRes),
        modifier = modifier.statusBarsPadding(),
    ) {
        PrimaryButton(text = stringResource(CoreUiR.string.action_try_again), onClick = onRetry)
    }
}

@Composable
private fun BackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
            .clickable(role = Role.Button, onClick = onClick),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_back),
            contentDescription = stringResource(R.string.detail_back),
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(22.dp),
        )
    }
}
