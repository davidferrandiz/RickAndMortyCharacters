package com.davidferrandiz.rickandmortycharacters.feature.detail

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.RickAndMortyTheme
import com.davidferrandiz.rickandmortycharacters.domain.error.AppError
import com.davidferrandiz.rickandmortycharacters.domain.model.Character
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterStatus
import com.davidferrandiz.rickandmortycharacters.domain.model.Episode
import com.davidferrandiz.rickandmortycharacters.domain.model.EpisodesState
import com.davidferrandiz.rickandmortycharacters.domain.model.Gender

@Preview(name = "Light", widthDp = 390, heightDp = 1000)
@Preview(name = "Dark", widthDp = 390, heightDp = 1000, uiMode = Configuration.UI_MODE_NIGHT_YES)
private annotation class ScreenPreviews

private val abradolf = Character(
    id = 7,
    name = "Abradolf Lincler",
    status = CharacterStatus.Unknown,
    species = "Human",
    type = "Genetic experiment",
    gender = Gender.Male,
    origin = "Earth (Replacement Dimension)",
    location = null,
    imageUrl = "",
    episodeIds = listOf(10, 11, 12, 13, 14),
)

private val episodes = listOf(
    Episode(id = 10, name = "Close Rick-counters of the Rick Kind", code = "S01E10"),
    Episode(id = 11, name = "Ricksy Business", code = "S01E11"),
    Episode(id = 12, name = "A Rickle in Time", code = "S02E01"),
    Episode(id = 13, name = "Mortynight Run", code = "S02E02"),
    Episode(id = 14, name = "Auto Erotic Assimilation", code = "S02E03"),
)

@Composable
private fun Screen(uiState: CharacterDetailUiState) {
    RickAndMortyTheme {
        CharacterDetailContent(
            characterId = abradolf.id,
            imageUrl = "",
            uiState = uiState,
            onBack = {},
            onRetry = {},
        )
    }
}

@PreviewTest
@ScreenPreviews
@Composable
fun CharacterDetail() {
    Screen(CharacterDetailUiState.Content(abradolf, EpisodesState.Loaded(episodes)))
}

@PreviewTest
@ScreenPreviews
@Composable
fun CharacterDetailEpisodesError() {
    Screen(CharacterDetailUiState.Content(abradolf, EpisodesState.Failed(AppError.Timeout)))
}

@PreviewTest
@ScreenPreviews
@Composable
fun CharacterDetailError() {
    Screen(CharacterDetailUiState.Error(AppError.NoConnection))
}
