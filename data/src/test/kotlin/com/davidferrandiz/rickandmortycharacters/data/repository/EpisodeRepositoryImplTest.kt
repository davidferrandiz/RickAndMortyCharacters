package com.davidferrandiz.rickandmortycharacters.data.repository

import com.davidferrandiz.rickandmortycharacters.data.remote.FakeRickAndMortyApi
import com.davidferrandiz.rickandmortycharacters.data.remote.model.EpisodeResponse
import com.davidferrandiz.rickandmortycharacters.domain.common.AppResult
import com.davidferrandiz.rickandmortycharacters.domain.model.Episode
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class EpisodeRepositoryImplTest {

    private val api = FakeRickAndMortyApi()
    private val repository = EpisodeRepositoryImpl(api)

    @Test
    fun `all episodes are requested in a single bracketed batch`() = runTest {
        api.episodes = { emptyList() }

        repository.getEpisodes(listOf(1, 2, 27))

        assertEquals(listOf("[1,2,27]"), api.episodeRequests)
    }

    @Test
    fun `a single episode is still requested as a batch`() = runTest {
        api.episodes = { listOf(EpisodeResponse(id = 27, name = "Rest and Ricklaxation", episode = "S03E06")) }

        val result = repository.getEpisodes(listOf(27))

        assertEquals(listOf("[27]"), api.episodeRequests)
        assertEquals(
            AppResult.Success(listOf(Episode(id = 27, name = "Rest and Ricklaxation", code = "S03E06"))),
            result,
        )
    }
}
