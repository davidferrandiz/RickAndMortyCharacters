package com.davidferrandiz.rickandmortycharacters.domain.repository

import com.davidferrandiz.rickandmortycharacters.domain.common.AppResult
import com.davidferrandiz.rickandmortycharacters.domain.model.Episode

interface EpisodeRepository {
    suspend fun getEpisodes(ids: List<Int>): AppResult<List<Episode>>
}
