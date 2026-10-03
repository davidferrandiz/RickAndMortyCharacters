package com.davidferrandiz.rickandmortycharacters.domain.model

import com.davidferrandiz.rickandmortycharacters.domain.common.AppResult

data class CharacterDetail(
    val character: Character,
    val episodes: AppResult<List<Episode>>?,
)
