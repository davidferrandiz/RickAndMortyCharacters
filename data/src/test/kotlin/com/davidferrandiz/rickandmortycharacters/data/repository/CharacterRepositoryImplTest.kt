package com.davidferrandiz.rickandmortycharacters.data.repository

import androidx.paging.PagingSource
import androidx.paging.testing.asSnapshot
import com.davidferrandiz.rickandmortycharacters.data.local.dao.CharacterDao
import com.davidferrandiz.rickandmortycharacters.data.local.dao.CharacterPagingDao
import com.davidferrandiz.rickandmortycharacters.data.local.entity.CharacterEntity
import com.davidferrandiz.rickandmortycharacters.data.local.entity.CharacterPagingEntity
import com.davidferrandiz.rickandmortycharacters.data.remote.CharactersRequest
import com.davidferrandiz.rickandmortycharacters.data.remote.FakeRickAndMortyApi
import com.davidferrandiz.rickandmortycharacters.data.remote.model.CharacterPageResponse
import com.davidferrandiz.rickandmortycharacters.data.remote.model.CharacterResponse
import com.davidferrandiz.rickandmortycharacters.data.remote.model.PageInfoResponse
import com.davidferrandiz.rickandmortycharacters.domain.common.AppResult
import com.davidferrandiz.rickandmortycharacters.domain.error.AppError
import com.davidferrandiz.rickandmortycharacters.domain.model.Character
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterFilter
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterStatus
import com.davidferrandiz.rickandmortycharacters.domain.model.Gender
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterRepositoryImplTest {

    private val api = FakeRickAndMortyApi()

    private fun repository(cached: CharacterEntity? = null) = CharacterRepositoryImpl(
        api = api,
        characterDao = FakeCharacterDao(cached),
        pagingDao = FakeCharacterPagingDao(),
        remoteMediator = { error("the cached list is not exercised in JVM tests") },
    )

    @Test
    fun `a cached character is returned without touching the network`() = runTest {
        val result = repository(cached = entity(id = 1, name = "Rick Sanchez")).getCharacter(1)

        assertEquals("Rick Sanchez", (result as AppResult.Success).data.name)
        assertTrue(api.characterRequests.isEmpty())
    }

    @Test
    fun `a character that is not cached is fetched from the network`() = runTest {
        api.character = { CharacterResponse(id = it, name = "Cop Morty", status = "Dead") }

        val result = repository(cached = null).getCharacter(74)

        val character = (result as AppResult.Success).data
        assertEquals("Cop Morty", character.name)
        assertEquals(CharacterStatus.Dead, character.status)
        assertEquals(listOf(74), api.characterRequests)
    }

    @Test
    fun `a network failure for an uncached character is an error`() = runTest {
        api.character = { throw IOException("network is unreachable") }

        val result = repository(cached = null).getCharacter(74)

        assertEquals(AppResult.Error(AppError.NoConnection), result)
    }

    @Test
    fun `a filtered list goes to the network with that filter`() = runTest {
        api.pages[1] = {
            CharacterPageResponse(
                info = PageInfoResponse(count = 1, next = null),
                results = listOf(CharacterResponse(id = 2, name = "Morty Smith")),
            )
        }

        val characters = repository().observeCharacters(CharacterFilter(name = "morty")).asSnapshot()

        assertEquals(listOf(2), characters.map(Character::id))
        assertEquals(CharactersRequest(page = 1, name = "morty", status = null, gender = null), api.requests.first())
    }
}

private fun entity(id: Int, name: String) = CharacterEntity(
    id = id,
    name = name,
    status = CharacterStatus.Alive,
    species = "Human",
    type = null,
    gender = Gender.Male,
    origin = "Earth (C-137)",
    location = "Citadel of Ricks",
    imageUrl = "",
    episodeIds = listOf(1),
)

private class FakeCharacterDao(private val cached: CharacterEntity?) : CharacterDao {
    override fun pagingSource(): PagingSource<Int, CharacterEntity> = error("not used")

    override suspend fun getById(id: Int): CharacterEntity? = cached?.takeIf { it.id == id }

    override suspend fun upsertAll(characters: List<CharacterEntity>) = error("not used")

    override suspend fun clear() = error("not used")
}

private class FakeCharacterPagingDao : CharacterPagingDao {
    override suspend fun get(): CharacterPagingEntity? = null

    override fun observeTotalCount(): Flow<Int?> = flowOf(null)

    override suspend fun upsert(paging: CharacterPagingEntity) = error("not used")

    override suspend fun clear() = error("not used")
}
