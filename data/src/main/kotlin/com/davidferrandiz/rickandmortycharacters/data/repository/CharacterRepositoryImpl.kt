package com.davidferrandiz.rickandmortycharacters.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.davidferrandiz.rickandmortycharacters.data.local.dao.CharacterDao
import com.davidferrandiz.rickandmortycharacters.data.local.dao.CharacterPagingDao
import com.davidferrandiz.rickandmortycharacters.data.local.entity.CharacterEntity
import com.davidferrandiz.rickandmortycharacters.data.mapper.toDomain
import com.davidferrandiz.rickandmortycharacters.data.paging.CharacterRemoteMediator
import com.davidferrandiz.rickandmortycharacters.data.paging.CharacterSearchPagingSource
import com.davidferrandiz.rickandmortycharacters.data.remote.RickAndMortyApi
import com.davidferrandiz.rickandmortycharacters.data.remote.safeApiCall
import com.davidferrandiz.rickandmortycharacters.domain.common.AppResult
import com.davidferrandiz.rickandmortycharacters.domain.model.Character
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterFilter
import com.davidferrandiz.rickandmortycharacters.domain.repository.CharacterRepository
import javax.inject.Inject
import javax.inject.Provider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val PAGE_SIZE = 20

internal class CharacterRepositoryImpl @Inject constructor(
    private val api: RickAndMortyApi,
    private val characterDao: CharacterDao,
    private val pagingDao: CharacterPagingDao,
    private val remoteMediator: Provider<CharacterRemoteMediator>,
) : CharacterRepository {

    private val pagingConfig = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false)

    override fun observeCharacters(filter: CharacterFilter): Flow<PagingData<Character>> =
        if (filter.isEmpty) cachedCharacters() else searchedCharacters(filter)

    override fun observeCharacterCount(): Flow<Int?> = pagingDao.observeTotalCount()

    override suspend fun getCharacter(id: Int): AppResult<Character> {
        val cached = characterDao.getById(id)
        return if (cached != null) {
            AppResult.Success(cached.toDomain())
        } else {
            safeApiCall { api.getCharacter(id).toDomain() }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun cachedCharacters(): Flow<PagingData<Character>> = Pager(
        config = pagingConfig,
        remoteMediator = remoteMediator.get(),
        pagingSourceFactory = characterDao::pagingSource,
    ).flow.map { page -> page.map(CharacterEntity::toDomain) }

    private fun searchedCharacters(filter: CharacterFilter): Flow<PagingData<Character>> = Pager(
        config = pagingConfig,
        pagingSourceFactory = { CharacterSearchPagingSource(api, filter) },
    ).flow
}
