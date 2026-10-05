package com.davidferrandiz.rickandmortycharacters.domain.usecase

import androidx.paging.PagingData
import com.davidferrandiz.rickandmortycharacters.domain.common.AppResult
import com.davidferrandiz.rickandmortycharacters.domain.error.AppError
import com.davidferrandiz.rickandmortycharacters.domain.model.Character
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterDetail
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterFilter
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterStatus
import com.davidferrandiz.rickandmortycharacters.domain.model.Episode
import com.davidferrandiz.rickandmortycharacters.domain.model.EpisodesState
import com.davidferrandiz.rickandmortycharacters.domain.model.Gender
import com.davidferrandiz.rickandmortycharacters.domain.repository.CharacterRepository
import com.davidferrandiz.rickandmortycharacters.domain.repository.EpisodeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetCharacterDetailUseCaseTest {

    private val rick = Character(
        id = 1,
        name = "Rick Sanchez",
        status = CharacterStatus.Alive,
        species = "Human",
        type = null,
        gender = Gender.Male,
        origin = "Earth (C-137)",
        location = "Citadel of Ricks",
        imageUrl = "",
        episodeIds = listOf(1, 2),
    )
    private val pilot = Episode(id = 1, name = "Pilot", code = "S01E01")

    @Test
    fun `character failure emits the error and never asks for episodes`() = runTest {
        val episodes = FakeEpisodeRepository(AppResult.Success(listOf(pilot)))
        val useCase = GetCharacterDetailUseCase(
            FakeCharacterRepository(AppResult.Error(AppError.NoConnection)),
            episodes,
        )

        val emissions = useCase(1).toList()

        assertEquals(listOf(AppResult.Error(AppError.NoConnection)), emissions)
        assertEquals(0, episodes.calls)
    }

    @Test
    fun `character is emitted before its episodes arrive`() = runTest {
        val useCase = GetCharacterDetailUseCase(
            FakeCharacterRepository(AppResult.Success(rick)),
            FakeEpisodeRepository(AppResult.Success(listOf(pilot))),
        )

        val emissions = useCase(1).toList()

        assertEquals(
            listOf(
                AppResult.Success(CharacterDetail(rick, episodes = EpisodesState.Loading)),
                AppResult.Success(CharacterDetail(rick, episodes = EpisodesState.Loaded(listOf(pilot)))),
            ),
            emissions,
        )
    }

    @Test
    fun `episodes failure keeps the character`() = runTest {
        val useCase = GetCharacterDetailUseCase(
            FakeCharacterRepository(AppResult.Success(rick)),
            FakeEpisodeRepository(AppResult.Error(AppError.Timeout)),
        )

        val last = useCase(1).toList().last()

        assertEquals(
            AppResult.Success(CharacterDetail(rick, episodes = EpisodesState.Failed(AppError.Timeout))),
            last,
        )
    }

    @Test
    fun `character without episodes resolves to an empty list without a request`() = runTest {
        val withoutEpisodes = rick.copy(episodeIds = emptyList())
        val episodes = FakeEpisodeRepository(AppResult.Success(listOf(pilot)))
        val useCase = GetCharacterDetailUseCase(
            FakeCharacterRepository(AppResult.Success(withoutEpisodes)),
            episodes,
        )

        val last = useCase(1).toList().last()

        assertEquals(
            AppResult.Success(CharacterDetail(withoutEpisodes, episodes = EpisodesState.Loaded(emptyList()))),
            last,
        )
        assertEquals(0, episodes.calls)
    }
}

private class FakeCharacterRepository(
    private val character: AppResult<Character>,
) : CharacterRepository {
    override fun observeCharacters(filter: CharacterFilter): Flow<PagingData<Character>> =
        flowOf(PagingData.empty())

    override fun observeCharacterCount(): Flow<Int?> = flowOf(null)

    override suspend fun getCharacter(id: Int): AppResult<Character> = character
}

private class FakeEpisodeRepository(
    private val episodes: AppResult<List<Episode>>,
) : EpisodeRepository {
    var calls = 0
        private set

    override suspend fun getEpisodes(ids: List<Int>): AppResult<List<Episode>> {
        calls++
        return episodes
    }
}
