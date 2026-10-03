package com.davidferrandiz.rickandmortycharacters.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.davidferrandiz.rickandmortycharacters.data.mapper.genderQuery
import com.davidferrandiz.rickandmortycharacters.data.mapper.nameQuery
import com.davidferrandiz.rickandmortycharacters.data.mapper.statusQuery
import com.davidferrandiz.rickandmortycharacters.data.mapper.toDomain
import com.davidferrandiz.rickandmortycharacters.data.remote.RickAndMortyApi
import com.davidferrandiz.rickandmortycharacters.data.remote.model.CharacterResponse
import com.davidferrandiz.rickandmortycharacters.data.remote.nextPage
import com.davidferrandiz.rickandmortycharacters.data.remote.safeApiCall
import com.davidferrandiz.rickandmortycharacters.domain.common.AppResult
import com.davidferrandiz.rickandmortycharacters.domain.error.AppError
import com.davidferrandiz.rickandmortycharacters.domain.error.AppErrorException
import com.davidferrandiz.rickandmortycharacters.domain.model.Character
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterFilter

private const val FIRST_PAGE = 1
private const val HTTP_NOT_FOUND = 404

internal class CharacterSearchPagingSource(
    private val api: RickAndMortyApi,
    private val filter: CharacterFilter,
) : PagingSource<Int, Character>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
        val response = safeApiCall {
            api.getCharacters(
                page = params.key ?: FIRST_PAGE,
                name = filter.nameQuery,
                status = filter.statusQuery,
                gender = filter.genderQuery,
            )
        }
        return when (response) {
            is AppResult.Success -> LoadResult.Page(
                data = response.data.results.map(CharacterResponse::toDomain),
                prevKey = null,
                nextKey = response.data.info.nextPage,
            )
            is AppResult.Error -> when (response.error) {
                AppError.Http(HTTP_NOT_FOUND) -> LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null,
                )
                else -> LoadResult.Error(AppErrorException(response.error))
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Character>): Int? = null
}
