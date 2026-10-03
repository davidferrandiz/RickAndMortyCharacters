package com.davidferrandiz.rickandmortycharacters.data.mapper

import com.davidferrandiz.rickandmortycharacters.data.remote.model.CharacterResponse
import com.davidferrandiz.rickandmortycharacters.data.remote.model.PlaceResponse
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterStatus
import com.davidferrandiz.rickandmortycharacters.domain.model.Gender
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CharacterMapperTest {

    private val response = CharacterResponse(
        id = 2,
        name = "Morty Smith",
        status = "Alive",
        species = "Human",
        type = "",
        gender = "Male",
        origin = PlaceResponse(name = "unknown"),
        location = PlaceResponse(name = "Citadel of Ricks"),
        image = "https://rickandmortyapi.com/api/character/avatar/2.jpeg",
        episode = listOf(
            "https://rickandmortyapi.com/api/episode/1",
            "https://rickandmortyapi.com/api/episode/27",
        ),
    )

    @Test
    fun `status is read whatever the casing the API uses`() {
        assertEquals(CharacterStatus.Alive, response.copy(status = "Alive").toDomain().status)
        assertEquals(CharacterStatus.Dead, response.copy(status = "dead").toDomain().status)
        assertEquals(CharacterStatus.Unknown, response.copy(status = "unknown").toDomain().status)
    }

    @Test
    fun `an unexpected or missing status falls back to Unknown`() {
        assertEquals(CharacterStatus.Unknown, response.copy(status = "Zombie").toDomain().status)
        assertEquals(CharacterStatus.Unknown, response.copy(status = "").toDomain().status)
    }

    @Test
    fun `gender is read whatever the casing and falls back to Unknown`() {
        assertEquals(Gender.Female, response.copy(gender = "Female").toDomain().gender)
        assertEquals(Gender.Male, response.copy(gender = "male").toDomain().gender)
        assertEquals(Gender.Genderless, response.copy(gender = "Genderless").toDomain().gender)
        assertEquals(Gender.Unknown, response.copy(gender = "unknown").toDomain().gender)
        assertEquals(Gender.Unknown, response.copy(gender = "Robot").toDomain().gender)
    }

    @Test
    fun `a blank type becomes null and a real one is kept`() {
        assertNull(response.copy(type = "").toDomain().type)
        assertNull(response.copy(type = "   ").toDomain().type)
        assertEquals("Genetic experiment", response.copy(type = "Genetic experiment").toDomain().type)
    }

    @Test
    fun `episode urls become ids and malformed ones are dropped`() {
        val character = response.copy(
            episode = listOf(
                "https://rickandmortyapi.com/api/episode/1",
                "https://rickandmortyapi.com/api/episode/",
                "not a url",
                "https://rickandmortyapi.com/api/episode/27",
            ),
        ).toDomain()

        assertEquals(listOf(1, 27), character.episodeIds)
    }

    @Test
    fun `wire vocabulary is translated to domain vocabulary`() {
        val character = response.toDomain()

        assertEquals("https://rickandmortyapi.com/api/character/avatar/2.jpeg", character.imageUrl)
        assertEquals("unknown", character.origin)
        assertEquals("Citadel of Ricks", character.location)
    }
}
