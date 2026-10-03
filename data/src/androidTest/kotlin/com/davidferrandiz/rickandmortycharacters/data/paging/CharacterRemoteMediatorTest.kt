package com.davidferrandiz.rickandmortycharacters.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator.InitializeAction
import androidx.paging.RemoteMediator.MediatorResult
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.davidferrandiz.rickandmortycharacters.data.local.RickAndMortyDatabase
import com.davidferrandiz.rickandmortycharacters.data.local.entity.CharacterEntity
import com.davidferrandiz.rickandmortycharacters.data.local.entity.CharacterPagingEntity
import com.davidferrandiz.rickandmortycharacters.data.remote.FakeRickAndMortyApi
import com.davidferrandiz.rickandmortycharacters.data.remote.model.CharacterPageResponse
import com.davidferrandiz.rickandmortycharacters.data.remote.model.CharacterResponse
import com.davidferrandiz.rickandmortycharacters.data.remote.model.PageInfoResponse
import com.davidferrandiz.rickandmortycharacters.domain.error.AppError
import com.davidferrandiz.rickandmortycharacters.domain.error.AppErrorException
import java.io.IOException
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalPagingApi::class)
@RunWith(AndroidJUnit4::class)
class CharacterRemoteMediatorTest {

    private val api = FakeRickAndMortyApi()
    private val clock = MutableClock(Instant.parse("2026-10-03T10:00:00Z"))
    private val emptyState = PagingState<Int, CharacterEntity>(
        pages = emptyList(),
        anchorPosition = null,
        config = PagingConfig(pageSize = 20),
        leadingPlaceholderCount = 0,
    )

    private lateinit var database: RickAndMortyDatabase
    private lateinit var mediator: CharacterRemoteMediator

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RickAndMortyDatabase::class.java,
        ).build()
        mediator = CharacterRemoteMediator(api, database, clock)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun withoutStoredStateTheFirstRefreshIsLaunched() = runBlocking {
        assertEquals(InitializeAction.LAUNCH_INITIAL_REFRESH, mediator.initialize())
    }

    @Test
    fun aCacheYoungerThanADaySkipsTheInitialRefresh() = runBlocking {
        refreshWith(page(ids = listOf(1, 2), next = 2))

        clock.advance(Duration.ofHours(23))

        assertEquals(InitializeAction.SKIP_INITIAL_REFRESH, mediator.initialize())
    }

    @Test
    fun aCacheOlderThanADayLaunchesTheInitialRefresh() = runBlocking {
        refreshWith(page(ids = listOf(1, 2), next = 2))

        clock.advance(Duration.ofHours(25))

        assertEquals(InitializeAction.LAUNCH_INITIAL_REFRESH, mediator.initialize())
    }

    @Test
    fun refreshStoresTheFirstPageAndThePagingState() = runBlocking {
        val result = refreshWith(page(ids = listOf(1, 2), next = 2, count = 826))

        assertEquals(false, (result as MediatorResult.Success).endOfPaginationReached)
        assertEquals("Character 1", database.characterDao().getById(1)?.name)
        assertEquals(
            CharacterPagingEntity(nextPage = 2, totalCount = 826, updatedAt = clock.millis()),
            database.characterPagingDao().get(),
        )
    }

    @Test
    fun refreshRevalidatesWithTheServerAndAppendDoesNot() = runBlocking {
        refreshWith(page(ids = listOf(1), next = 2))
        api.pages[2] = { page(ids = listOf(3), next = null) }

        mediator.load(LoadType.APPEND, emptyState)

        assertEquals(listOf("no-cache", null), api.requests.map { it.cacheControl })
    }

    @Test
    fun appendLoadsTheStoredNextPageAndKeepsWhatWasCached() = runBlocking {
        refreshWith(page(ids = listOf(1, 2), next = 2))
        val refreshedAt = clock.millis()
        clock.advance(Duration.ofHours(1))
        api.pages[2] = { page(ids = listOf(3, 4), next = 3) }

        val result = mediator.load(LoadType.APPEND, emptyState)

        assertEquals(false, (result as MediatorResult.Success).endOfPaginationReached)
        assertEquals(listOf(1, 2), api.requests.map { it.page })
        assertNotNull(database.characterDao().getById(1))
        assertNotNull(database.characterDao().getById(4))
        assertEquals(3, database.characterPagingDao().get()?.nextPage)
        assertEquals(refreshedAt, database.characterPagingDao().get()?.updatedAt)
    }

    @Test
    fun theLastPageEndsThePaginationAndFurtherAppendsDoNotHitTheNetwork() = runBlocking {
        refreshWith(page(ids = listOf(1), next = 2))
        api.pages[2] = { page(ids = listOf(2), next = null) }

        val lastPage = mediator.load(LoadType.APPEND, emptyState)
        val afterTheEnd = mediator.load(LoadType.APPEND, emptyState)

        assertTrue((lastPage as MediatorResult.Success).endOfPaginationReached)
        assertTrue((afterTheEnd as MediatorResult.Success).endOfPaginationReached)
        assertEquals(2, api.requests.size)
    }

    @Test
    fun aFailedRefreshKeepsTheCachedCharacters() = runBlocking {
        refreshWith(page(ids = listOf(1, 2), next = 2))
        api.pages[1] = { throw IOException("network is unreachable") }

        val result = mediator.load(LoadType.REFRESH, emptyState)

        assertEquals(AppError.NoConnection, ((result as MediatorResult.Error).throwable as AppErrorException).error)
        assertNotNull(database.characterDao().getById(1))
        assertNotNull(database.characterDao().getById(2))
        assertEquals(2, database.characterPagingDao().get()?.nextPage)
    }

    @Test
    fun refreshReplacesTheCacheInsteadOfAddingToIt() = runBlocking {
        refreshWith(page(ids = listOf(1, 2), next = 2))
        api.pages[2] = { page(ids = listOf(3, 4), next = 3) }
        mediator.load(LoadType.APPEND, emptyState)

        refreshWith(page(ids = listOf(1, 2), next = 2))

        assertNotNull(database.characterDao().getById(1))
        assertNull(database.characterDao().getById(3))
        assertEquals(2, database.characterPagingDao().get()?.nextPage)
    }

    @Test
    fun prependEndsImmediatelyWithoutANetworkCall() = runBlocking {
        val result = mediator.load(LoadType.PREPEND, emptyState)

        assertTrue((result as MediatorResult.Success).endOfPaginationReached)
        assertTrue(api.requests.isEmpty())
    }

    private suspend fun refreshWith(page: CharacterPageResponse): MediatorResult {
        api.pages[1] = { page }
        return mediator.load(LoadType.REFRESH, emptyState)
    }
}

private fun page(ids: List<Int>, next: Int?, count: Int = 826) = CharacterPageResponse(
    info = PageInfoResponse(
        count = count,
        next = next?.let { "https://rickandmortyapi.com/api/character?page=$it" },
    ),
    results = ids.map { CharacterResponse(id = it, name = "Character $it") },
)

private class MutableClock(private var now: Instant) : Clock() {
    fun advance(duration: Duration) {
        now += duration
    }

    override fun instant(): Instant = now

    override fun getZone(): ZoneId = ZoneOffset.UTC

    override fun withZone(zone: ZoneId): Clock = this
}
