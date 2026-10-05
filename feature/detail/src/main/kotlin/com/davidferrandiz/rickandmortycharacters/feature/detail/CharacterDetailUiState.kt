package com.davidferrandiz.rickandmortycharacters.feature.detail

import com.davidferrandiz.rickandmortycharacters.domain.common.AppResult
import com.davidferrandiz.rickandmortycharacters.domain.error.AppError
import com.davidferrandiz.rickandmortycharacters.domain.model.Character
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterDetail
import com.davidferrandiz.rickandmortycharacters.domain.model.EpisodesState

sealed interface CharacterDetailUiState {
    data object Loading : CharacterDetailUiState
    data class Error(val error: AppError) : CharacterDetailUiState
    data class Content(val character: Character, val episodes: EpisodesState) : CharacterDetailUiState
}

internal fun AppResult<CharacterDetail>.toUiState(): CharacterDetailUiState = when (this) {
    is AppResult.Error -> CharacterDetailUiState.Error(error)
    is AppResult.Success -> CharacterDetailUiState.Content(data.character, data.episodes)
}
