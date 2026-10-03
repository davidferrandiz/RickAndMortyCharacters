package com.davidferrandiz.rickandmortycharacters.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.davidferrandiz.rickandmortycharacters.data.local.RickAndMortyDatabase
import com.davidferrandiz.rickandmortycharacters.data.local.entity.CharacterEntity
import com.davidferrandiz.rickandmortycharacters.data.local.entity.CharacterPagingEntity
import com.davidferrandiz.rickandmortycharacters.data.mapper.toEntity
import com.davidferrandiz.rickandmortycharacters.data.remote.RickAndMortyApi
import com.davidferrandiz.rickandmortycharacters.data.remote.model.CharacterPageResponse
import com.davidferrandiz.rickandmortycharacters.data.remote.model.CharacterResponse
import com.davidferrandiz.rickandmortycharacters.data.remote.nextPage
import com.davidferrandiz.rickandmortycharacters.data.remote.safeApiCall
import com.davidferrandiz.rickandmortycharacters.domain.common.AppResult
import com.davidferrandiz.rickandmortycharacters.domain.error.AppErrorException
import java.time.Clock
import java.time.Duration
import javax.inject.Inject

private const val FIRST_PAGE = 1
private const val REVALIDATE = "no-cache"
private val CACHE_VALIDITY = Duration.ofHours(24)

@OptIn(ExperimentalPagingApi::class)
internal class CharacterRemoteMediator @Inject constructor(
    private val api: RickAndMortyApi,
    private val database: RickAndMortyDatabase,
    private val clock: Clock,
) : RemoteMediator<Int, CharacterEntity>() {

    private val characterDao = database.characterDao()
    private val pagingDao = database.characterPagingDao()

    override suspend fun initialize(): InitializeAction {
        val updatedAt = pagingDao.get()?.updatedAt
        val isFresh = updatedAt != null && clock.millis() - updatedAt < CACHE_VALIDITY.toMillis()
        return if (isFresh) InitializeAction.SKIP_INITIAL_REFRESH else InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterEntity>,
    ): MediatorResult {
        val current = pagingDao.get()
        val page = when (loadType) {
            LoadType.REFRESH -> FIRST_PAGE
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> current?.nextPage
                ?: return MediatorResult.Success(endOfPaginationReached = true)
        }
        val isRefresh = loadType == LoadType.REFRESH
        val response = safeApiCall {
            api.getCharacters(page = page, cacheControl = if (isRefresh) REVALIDATE else null)
        }
        return when (response) {
            is AppResult.Error -> MediatorResult.Error(AppErrorException(response.error))
            is AppResult.Success -> {
                val updatedAt = if (isRefresh || current == null) clock.millis() else current.updatedAt
                store(response.data, replace = isRefresh, updatedAt = updatedAt)
                MediatorResult.Success(endOfPaginationReached = response.data.info.nextPage == null)
            }
        }
    }

    private suspend fun store(page: CharacterPageResponse, replace: Boolean, updatedAt: Long) {
        database.withTransaction {
            if (replace) characterDao.clear()
            characterDao.upsertAll(page.results.map(CharacterResponse::toEntity))
            pagingDao.upsert(
                CharacterPagingEntity(
                    nextPage = page.info.nextPage,
                    totalCount = page.info.count,
                    updatedAt = updatedAt,
                ),
            )
        }
    }
}
