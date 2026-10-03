package com.davidferrandiz.rickandmortycharacters.data.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource.LoadResult
import androidx.paging.testing.TestPager
import com.davidferrandiz.rickandmortycharacters.data.remote.RickAndMortyApi
import com.davidferrandiz.rickandmortycharacters.data.remote.model.CharacterPageResponse
import com.davidferrandiz.rickandmortycharacters.data.remote.model.CharacterResponse
import com.davidferrandiz.rickandmortycharacters.data.remote.model.EpisodeResponse
import com.davidferrandiz.rickandmortycharacters.data.remote.model.PageInfoResponse
import com.davidferrandiz.rickandmortycharacters.domain.error.AppError
import com.davidferrandiz.rickandmortycharacters.domain.error.AppErrorException
import com.davidferrandiz.rickandmortycharacters.domain.model.Character
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterFilter
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterStatus
import com.davidferrandiz.rickandmortycharacters.domain.model.Gender
import java.io.IOException
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class CharacterSearchPagingSourceTest {

    private val api = FakeRickAndMortyApi()
    private val config = PagingConfig(pageSize = 20)

    private fun pager(filter: CharacterFilter = CharacterFilter(name = "morty")) =
        TestPager(config, CharacterSearchPagingSource(api, filter))

    @Test
    fun `first page is mapped to domain and points to the next one`() = runTest {
        api.pages[1] = { page(ids = listOf(2, 14), next = "https://rickandmortyapi.com/api/character?page=2&name=morty") }

        val result = pager().refresh() as LoadResult.Page<Int, Character>

        assertEquals(listOf(2, 14), result.data.map(Character::id))
        assertNull(result.prevKey)
        assertEquals(2, result.nextKey)
    }

    @Test
    fun `a search without matches is an empty page and not an error`() = runTest {
        api.pages[1] = { throw notFound() }

        val result = pager().refresh()

        assertEquals(LoadResult.Page<Int, Character>(emptyList(), prevKey = null, nextKey = null), result)
    }

    @Test
    fun `a 404 on a later page ends the pagination and keeps what was loaded`() = runTest {
        api.pages[1] = { page(ids = listOf(2), next = "https://rickandmortyapi.com/api/character?page=2&name=morty") }
        api.pages[2] = { throw notFound() }
        val pager = pager()

        pager.refresh()
        val appended = pager.append() as LoadResult.Page<Int, Character>

        assertTrue(appended.data.isEmpty())
        assertNull(appended.nextKey)
        assertEquals(listOf(2), pager.getPages().flatMap { it.data }.map(Character::id))
    }

    @Test
    fun `the last page has no next key`() = runTest {
        api.pages[1] = { page(ids = listOf(2), next = null) }

        val result = pager().refresh() as LoadResult.Page<Int, Character>

        assertNull(result.nextKey)
    }

    @Test
    fun `any other failure is an error that carries the AppError`() = runTest {
        api.pages[1] = { throw IOException("network is unreachable") }

        val result = pager().refresh() as LoadResult.Error<Int, Character>

        assertEquals(AppError.NoConnection, (result.throwable as AppErrorException).error)
    }

    @Test
    fun `filters are sent trimmed and in the casing the API documents`() = runTest {
        api.pages[1] = { page(ids = emptyList(), next = null) }
        val filter = CharacterFilter(name = "  Morty ", status = CharacterStatus.Unknown, gender = Gender.Genderless)

        pager(filter).refresh()

        assertEquals(Request(page = 1, name = "Morty", status = "unknown", gender = "genderless"), api.requests.single())
    }

    @Test
    fun `unset filters are not sent at all`() = runTest {
        api.pages[1] = { page(ids = emptyList(), next = null) }

        pager(CharacterFilter(name = "   ", status = CharacterStatus.Dead)).refresh()

        assertEquals(Request(page = 1, name = null, status = "dead", gender = null), api.requests.single())
    }
}

private data class Request(val page: Int, val name: String?, val status: String?, val gender: String?)

private class FakeRickAndMortyApi : RickAndMortyApi {
    val pages = mutableMapOf<Int, () -> CharacterPageResponse>()
    val requests = mutableListOf<Request>()

    override suspend fun getCharacters(
        page: Int,
        name: String?,
        status: String?,
        gender: String?,
        cacheControl: String?,
    ): CharacterPageResponse {
        requests += Request(page, name, status, gender)
        return pages.getValue(page).invoke()
    }

    override suspend fun getCharacter(id: Int): CharacterResponse = error("not used")

    override suspend fun getEpisodes(ids: String): List<EpisodeResponse> = error("not used")
}

private fun page(ids: List<Int>, next: String?) = CharacterPageResponse(
    info = PageInfoResponse(count = ids.size, next = next),
    results = ids.map { CharacterResponse(id = it, name = "Character $it") },
)

private fun notFound() = HttpException(
    Response.error<Unit>(404, """{"error":"There is nothing here"}""".toResponseBody("application/json".toMediaType())),
)
