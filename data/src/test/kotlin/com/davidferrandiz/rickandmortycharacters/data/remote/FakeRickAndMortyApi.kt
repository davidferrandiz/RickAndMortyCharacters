package com.davidferrandiz.rickandmortycharacters.data.remote

import com.davidferrandiz.rickandmortycharacters.data.remote.model.CharacterPageResponse
import com.davidferrandiz.rickandmortycharacters.data.remote.model.CharacterResponse
import com.davidferrandiz.rickandmortycharacters.data.remote.model.EpisodeResponse

internal data class CharactersRequest(
    val page: Int,
    val name: String?,
    val status: String?,
    val gender: String?,
)

internal class FakeRickAndMortyApi : RickAndMortyApi {
    val pages = mutableMapOf<Int, () -> CharacterPageResponse>()
    val requests = mutableListOf<CharactersRequest>()

    var character: (Int) -> CharacterResponse = { error("getCharacter is not stubbed") }
    val characterRequests = mutableListOf<Int>()

    var episodes: (String) -> List<EpisodeResponse> = { error("getEpisodes is not stubbed") }
    val episodeRequests = mutableListOf<String>()

    override suspend fun getCharacters(
        page: Int,
        name: String?,
        status: String?,
        gender: String?,
        cacheControl: String?,
    ): CharacterPageResponse {
        requests += CharactersRequest(page, name, status, gender)
        return pages.getValue(page).invoke()
    }

    override suspend fun getCharacter(id: Int): CharacterResponse {
        characterRequests += id
        return character(id)
    }

    override suspend fun getEpisodes(ids: String): List<EpisodeResponse> {
        episodeRequests += ids
        return episodes(ids)
    }
}
