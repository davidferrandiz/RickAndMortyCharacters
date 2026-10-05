package com.davidferrandiz.rickandmortycharacters.feature.detail

import androidx.paging.PagingData
import com.davidferrandiz.rickandmortycharacters.domain.common.AppResult
import com.davidferrandiz.rickandmortycharacters.domain.error.AppError
import com.davidferrandiz.rickandmortycharacters.domain.model.Character
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterFilter
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterStatus
import com.davidferrandiz.rickandmortycharacters.domain.model.Episode
import com.davidferrandiz.rickandmortycharacters.domain.model.EpisodesState
import com.davidferrandiz.rickandmortycharacters.domain.model.Gender
import com.davidferrandiz.rickandmortycharacters.domain.repository.CharacterRepository
import com.davidferrandiz.rickandmortycharacters.domain.repository.EpisodeRepository
import com.davidferrandiz.rickandmortycharacters.domain.usecase.GetCharacterDetailUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

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
        episodeIds = listOf(1),
    )
    private val pilot = Episode(id = 1, name = "Pilot", code = "S01E01")

    private val characters = FakeCharacterRepository(AppResult.Success(rick))
    private val episodes = FakeEpisodeRepository(AppResult.Success(listOf(pilot)))

    private fun TestScope.observedStates(): List<CharacterDetailUiState> {
        val viewModel = CharacterDetailViewModel(1, GetCharacterDetailUseCase(characters, episodes))
        val states = mutableListOf<CharacterDetailUiState>()
        backgroundScope.launch { viewModel.uiState.collect(states::add) }
        retry = viewModel::onRetry
        return states
    }

    private var retry: () -> Unit = {}

    @Test
    fun `starts loading and ends with the character and its episodes`() = runTest {
        val states = observedStates()
        runCurrent()

        assertEquals(CharacterDetailUiState.Loading, states.first())
        assertEquals(
            CharacterDetailUiState.Content(rick, EpisodesState.Loaded(listOf(pilot))),
            states.last(),
        )
    }

    @Test
    fun `a character failure is the error state`() = runTest {
        characters.result = AppResult.Error(AppError.NoConnection)

        val states = observedStates()
        runCurrent()

        assertEquals(CharacterDetailUiState.Error(AppError.NoConnection), states.last())
    }

    @Test
    fun `an episodes failure keeps the character on screen`() = runTest {
        episodes.result = AppResult.Error(AppError.Timeout)

        val states = observedStates()
        runCurrent()

        assertEquals(
            CharacterDetailUiState.Content(rick, EpisodesState.Failed(AppError.Timeout)),
            states.last(),
        )
    }

    @Test
    fun `retry loads again and recovers from the error`() = runTest {
        characters.result = AppResult.Error(AppError.NoConnection)
        val states = observedStates()
        runCurrent()

        characters.result = AppResult.Success(rick)
        retry()
        runCurrent()

        assertEquals(2, characters.calls)
        assertEquals(
            CharacterDetailUiState.Content(rick, EpisodesState.Loaded(listOf(pilot))),
            states.last(),
        )
    }
}

private class FakeCharacterRepository(var result: AppResult<Character>) : CharacterRepository {
    var calls = 0
        private set

    override fun observeCharacters(filter: CharacterFilter): Flow<PagingData<Character>> =
        flowOf(PagingData.empty())

    override fun observeCharacterCount(): Flow<Int?> = flowOf(null)

    override suspend fun getCharacter(id: Int): AppResult<Character> {
        calls++
        return result
    }
}

private class FakeEpisodeRepository(var result: AppResult<List<Episode>>) : EpisodeRepository {
    override suspend fun getEpisodes(ids: List<Int>): AppResult<List<Episode>> = result
}
