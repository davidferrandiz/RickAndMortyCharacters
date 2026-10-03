package com.davidferrandiz.rickandmortycharacters.domain.repository

import androidx.paging.PagingData
import com.davidferrandiz.rickandmortycharacters.domain.common.AppResult
import com.davidferrandiz.rickandmortycharacters.domain.model.Character
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterFilter
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    fun observeCharacters(filter: CharacterFilter): Flow<PagingData<Character>>
    fun observeCharacterCount(): Flow<Int?>
    suspend fun getCharacter(id: Int): AppResult<Character>
}
