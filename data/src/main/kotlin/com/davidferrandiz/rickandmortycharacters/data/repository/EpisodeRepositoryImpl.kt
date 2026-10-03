package com.davidferrandiz.rickandmortycharacters.data.repository

import com.davidferrandiz.rickandmortycharacters.data.mapper.toDomain
import com.davidferrandiz.rickandmortycharacters.data.remote.RickAndMortyApi
import com.davidferrandiz.rickandmortycharacters.data.remote.model.EpisodeResponse
import com.davidferrandiz.rickandmortycharacters.data.remote.safeApiCall
import com.davidferrandiz.rickandmortycharacters.domain.common.AppResult
import com.davidferrandiz.rickandmortycharacters.domain.model.Episode
import com.davidferrandiz.rickandmortycharacters.domain.repository.EpisodeRepository
import javax.inject.Inject

internal class EpisodeRepositoryImpl @Inject constructor(
    private val api: RickAndMortyApi,
) : EpisodeRepository {

    override suspend fun getEpisodes(ids: List<Int>): AppResult<List<Episode>> = safeApiCall {
        api.getEpisodes(ids.toBatchPath()).map(EpisodeResponse::toDomain)
    }
}

private fun List<Int>.toBatchPath(): String = joinToString(separator = ",", prefix = "[", postfix = "]")
