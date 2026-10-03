package com.davidferrandiz.rickandmortycharacters.feature.detail

import com.davidferrandiz.rickandmortycharacters.domain.common.AppResult
import com.davidferrandiz.rickandmortycharacters.domain.error.AppError
import com.davidferrandiz.rickandmortycharacters.domain.model.Character
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterDetail
import com.davidferrandiz.rickandmortycharacters.domain.model.Episode

sealed interface CharacterDetailUiState {
    data object Loading : CharacterDetailUiState
    data class Error(val error: AppError) : CharacterDetailUiState
    data class Content(val character: Character, val episodes: EpisodesUiState) : CharacterDetailUiState
}

sealed interface EpisodesUiState {
    data object Loading : EpisodesUiState
    data class Error(val error: AppError) : EpisodesUiState
    data class Content(val episodes: List<Episode>) : EpisodesUiState
}

internal fun AppResult<CharacterDetail>.toUiState(): CharacterDetailUiState = when (this) {
    is AppResult.Error -> CharacterDetailUiState.Error(error)
    is AppResult.Success -> CharacterDetailUiState.Content(
        character = data.character,
        episodes = when (val episodes = data.episodes) {
            null -> EpisodesUiState.Loading
            is AppResult.Error -> EpisodesUiState.Error(episodes.error)
            is AppResult.Success -> EpisodesUiState.Content(episodes.data)
        },
    )
}
