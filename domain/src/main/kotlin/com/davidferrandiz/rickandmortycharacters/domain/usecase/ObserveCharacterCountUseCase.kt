package com.davidferrandiz.rickandmortycharacters.domain.usecase

import com.davidferrandiz.rickandmortycharacters.domain.repository.CharacterRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveCharacterCountUseCase @Inject constructor(
    private val characterRepository: CharacterRepository,
) {
    operator fun invoke(): Flow<Int?> = characterRepository.observeCharacterCount()
}
