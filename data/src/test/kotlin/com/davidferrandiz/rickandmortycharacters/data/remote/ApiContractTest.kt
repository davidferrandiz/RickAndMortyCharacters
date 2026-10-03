package com.davidferrandiz.rickandmortycharacters.data.remote

import com.davidferrandiz.rickandmortycharacters.data.di.NetworkModule
import com.davidferrandiz.rickandmortycharacters.domain.common.AppResult
import com.davidferrandiz.rickandmortycharacters.domain.error.AppError
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerializationException
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.Cache
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ApiContractTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val server = MockWebServer()
    private lateinit var api: RickAndMortyApi

    @Before
    fun setUp() {
        server.start()
        val client = NetworkModule.provideOkHttpClient(Cache(temporaryFolder.newFolder(), CACHE_SIZE_BYTES))
        val retrofit = NetworkModule.retrofit(server.url("/api/"), NetworkModule.provideJson(), client)
        api = NetworkModule.provideRickAndMortyApi(retrofit)
    }

    @After
    fun tearDown() {
        server.close()
    }

    @Test
    fun `parses a character page ignoring the fields the app does not use`() = runBlocking {
        server.enqueue(MockResponse(body = CHARACTER_PAGE))

        val page = api.getCharacters(page = 1)

        assertEquals(826, page.info.count)
        assertEquals("https://rickandmortyapi.com/api/character?page=2", page.info.next)
        val morty = page.results.single()
        assertEquals(2, morty.id)
        assertEquals("Morty Smith", morty.name)
        assertEquals("Alive", morty.status)
        assertEquals("", morty.type)
        assertEquals("unknown", morty.origin.name)
        assertEquals("https://rickandmortyapi.com/api/character/avatar/2.jpeg", morty.image)
        assertEquals(listOf("https://rickandmortyapi.com/api/episode/1"), morty.episode)
    }

    @Test
    fun `falls back to defaults when optional fields are missing or null`() = runBlocking {
        server.enqueue(MockResponse(body = """{"id":7,"name":"Abradolf Lincler","type":null}"""))

        val character = api.getCharacter(7)

        assertEquals("", character.type)
        assertEquals("", character.status)
        assertEquals("", character.origin.name)
        assertEquals(emptyList<String>(), character.episode)
    }

    @Test
    fun `fails instead of inventing a value when a guaranteed field is missing`() {
        server.enqueue(MockResponse(body = """{"name":"Nameless"}"""))

        assertThrows(SerializationException::class.java) {
            runBlocking { api.getCharacter(1) }
        }
    }

    @Test
    fun `a search without matches surfaces as HTTP 404`() = runBlocking {
        server.enqueue(MockResponse(code = 404, body = """{"error":"There is nothing here"}"""))

        val result = safeApiCall { api.getCharacters(page = 1, name = "zzzzqq") }

        assertEquals(AppResult.Error(AppError.Http(404)), result)
    }

    @Test
    fun `sends only the filters that are set`() = runBlocking {
        server.enqueue(MockResponse(body = CHARACTER_PAGE))

        api.getCharacters(page = 3, name = "morty", gender = "male")

        val url = server.takeRequest().url
        assertEquals("/api/character", url.encodedPath)
        assertEquals(setOf("page", "name", "gender"), url.queryParameterNames)
        assertEquals("3", url.queryParameter("page"))
        assertEquals("morty", url.queryParameter("name"))
        assertEquals("male", url.queryParameter("gender"))
    }

    @Test
    fun `requests episodes with brackets so a single id still returns a list`() = runBlocking {
        server.enqueue(MockResponse(body = SINGLE_EPISODE_ARRAY))

        val episodes = api.getEpisodes("[1]")

        assertEquals("/api/episode/[1]", server.takeRequest().url.encodedPath)
        assertEquals("S01E01", episodes.single().episode)
    }

    @Test
    fun `an episode batch with no valid ids is an empty list and not an error`() = runBlocking {
        server.enqueue(MockResponse(body = "[]"))

        val episodes = api.getEpisodes("[99999]")

        assertTrue(episodes.isEmpty())
    }

    @Test
    fun `serves a repeated request from the HTTP cache the server allows`() = runBlocking {
        server.enqueue(
            MockResponse.Builder()
                .addHeader("Cache-Control", "public, max-age=7776000, immutable")
                .body(SINGLE_EPISODE_ARRAY)
                .build(),
        )

        val first = api.getEpisodes("[1]")
        val second = api.getEpisodes("[1]")

        assertEquals(first, second)
        assertEquals(1, server.requestCount)
    }
}

private const val CACHE_SIZE_BYTES = 1024L * 1024

private const val CHARACTER_PAGE = """
{
  "info": {
    "count": 826,
    "pages": 42,
    "next": "https://rickandmortyapi.com/api/character?page=2",
    "prev": null
  },
  "results": [
    {
      "id": 2,
      "name": "Morty Smith",
      "status": "Alive",
      "species": "Human",
      "type": "",
      "gender": "Male",
      "origin": { "name": "unknown", "url": "" },
      "location": { "name": "Citadel of Ricks", "url": "https://rickandmortyapi.com/api/location/3" },
      "image": "https://rickandmortyapi.com/api/character/avatar/2.jpeg",
      "episode": ["https://rickandmortyapi.com/api/episode/1"],
      "url": "https://rickandmortyapi.com/api/character/2",
      "created": "2017-11-04T18:50:21.651Z"
    }
  ]
}
"""

private const val SINGLE_EPISODE_ARRAY = """
[
  {
    "id": 1,
    "name": "Pilot",
    "air_date": "December 2, 2013",
    "episode": "S01E01",
    "characters": ["https://rickandmortyapi.com/api/character/1"],
    "url": "https://rickandmortyapi.com/api/episode/1",
    "created": "2017-11-10T12:56:33.798Z"
  }
]
"""
