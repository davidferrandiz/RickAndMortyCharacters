package com.davidferrandiz.rickandmortycharacters.domain.usecase

import androidx.paging.PagingData
import com.davidferrandiz.rickandmortycharacters.domain.model.Character
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterFilter
import com.davidferrandiz.rickandmortycharacters.domain.repository.CharacterRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveCharactersUseCase @Inject constructor(
    private val characterRepository: CharacterRepository,
) {
    operator fun invoke(filter: CharacterFilter): Flow<PagingData<Character>> =
        characterRepository.observeCharacters(filter)
}
