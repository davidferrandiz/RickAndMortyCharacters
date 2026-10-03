package com.davidferrandiz.rickandmortycharacters.domain.usecase

import com.davidferrandiz.rickandmortycharacters.domain.common.AppResult
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterDetail
import com.davidferrandiz.rickandmortycharacters.domain.model.Episode
import com.davidferrandiz.rickandmortycharacters.domain.repository.CharacterRepository
import com.davidferrandiz.rickandmortycharacters.domain.repository.EpisodeRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetCharacterDetailUseCase @Inject constructor(
    private val characterRepository: CharacterRepository,
    private val episodeRepository: EpisodeRepository,
) {
    operator fun invoke(id: Int): Flow<AppResult<CharacterDetail>> = flow {
        when (val character = characterRepository.getCharacter(id)) {
            is AppResult.Error -> emit(character)
            is AppResult.Success -> {
                val detail = CharacterDetail(character = character.data, episodes = null)
                emit(AppResult.Success(detail))
                emit(AppResult.Success(detail.copy(episodes = loadEpisodes(character.data.episodeIds))))
            }
        }
    }

    private suspend fun loadEpisodes(ids: List<Int>): AppResult<List<Episode>> =
        if (ids.isEmpty()) AppResult.Success(emptyList()) else episodeRepository.getEpisodes(ids)
}
