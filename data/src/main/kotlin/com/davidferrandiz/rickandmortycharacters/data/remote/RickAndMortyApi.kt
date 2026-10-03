package com.davidferrandiz.rickandmortycharacters.data.remote

import com.davidferrandiz.rickandmortycharacters.data.remote.model.CharacterPageResponse
import com.davidferrandiz.rickandmortycharacters.data.remote.model.CharacterResponse
import com.davidferrandiz.rickandmortycharacters.data.remote.model.EpisodeResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface RickAndMortyApi {

    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int,
        @Query("name") name: String? = null,
        @Query("status") status: String? = null,
        @Query("gender") gender: String? = null,
    ): CharacterPageResponse

    @GET("character/{id}")
    suspend fun getCharacter(@Path("id") id: Int): CharacterResponse

    @GET("episode/{ids}")
    suspend fun getEpisodes(@Path("ids", encoded = true) ids: String): List<EpisodeResponse>
}
