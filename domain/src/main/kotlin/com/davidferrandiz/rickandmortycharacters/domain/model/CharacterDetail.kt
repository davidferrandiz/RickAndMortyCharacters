package com.davidferrandiz.rickandmortycharacters.domain.model

import com.davidferrandiz.rickandmortycharacters.domain.error.AppError

data class CharacterDetail(
    val character: Character,
    val episodes: EpisodesState,
)

sealed interface EpisodesState {
    data object Loading : EpisodesState
    data class Loaded(val episodes: List<Episode>) : EpisodesState
    data class Failed(val error: AppError) : EpisodesState
}
