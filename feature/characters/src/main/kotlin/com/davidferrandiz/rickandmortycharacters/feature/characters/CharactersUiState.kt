package com.davidferrandiz.rickandmortycharacters.feature.characters

import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterStatus
import com.davidferrandiz.rickandmortycharacters.domain.model.Gender

data class CharactersUiState(
    val status: CharacterStatus? = null,
    val gender: Gender? = null,
    val totalCount: Int? = null,
)
