package com.davidferrandiz.rickandmortycharacters.feature.characters

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import com.davidferrandiz.rickandmortycharacters.domain.common.AppResult
import com.davidferrandiz.rickandmortycharacters.domain.model.Character
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterFilter
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterStatus
import com.davidferrandiz.rickandmortycharacters.domain.model.Gender
import com.davidferrandiz.rickandmortycharacters.domain.repository.CharacterRepository
import com.davidferrandiz.rickandmortycharacters.domain.usecase.ObserveCharacterCountUseCase
import com.davidferrandiz.rickandmortycharacters.domain.usecase.ObserveCharactersUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharactersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeCharacterRepository()

    private fun TestScope.observedViewModel(
        savedStateHandle: SavedStateHandle = SavedStateHandle(),
    ): CharactersViewModel {
        val viewModel = CharactersViewModel(
            savedStateHandle = savedStateHandle,
            observeCharacters = ObserveCharactersUseCase(repository),
            observeCharacterCount = ObserveCharacterCountUseCase(repository),
        )
        backgroundScope.launch { viewModel.characters.collect() }
        backgroundScope.launch { viewModel.uiState.collect() }
        return viewModel
    }

    @Test
    fun `the unfiltered list is requested straight away`() = runTest {
        observedViewModel()
        runCurrent()

        assertEquals(listOf(CharacterFilter()), repository.requestedFilters)
    }

    @Test
    fun `fast typing collapses into a single search once the user pauses`() = runTest {
        val viewModel = observedViewModel()
        runCurrent()

        viewModel.onQueryChange("m")
        advanceTimeBy(100)
        viewModel.onQueryChange("mo")
        advanceTimeBy(100)
        viewModel.onQueryChange("mor")
        advanceTimeBy(299)

        assertEquals(listOf(CharacterFilter()), repository.requestedFilters)

        advanceTimeBy(2)

        assertEquals(listOf(CharacterFilter(), CharacterFilter(name = "mor")), repository.requestedFilters)
    }

    @Test
    fun `a status chip applies without waiting for the debounce`() = runTest {
        val viewModel = observedViewModel()
        runCurrent()

        viewModel.onStatusSelect(CharacterStatus.Dead)
        runCurrent()

        assertEquals(CharacterFilter(status = CharacterStatus.Dead), repository.requestedFilters.last())
    }

    @Test
    fun `search, status and gender combine into one filter`() = runTest {
        val viewModel = observedViewModel()
        runCurrent()

        viewModel.onQueryChange("morty")
        viewModel.onFiltersApply(CharacterStatus.Alive, Gender.Male)
        advanceTimeBy(301)

        assertEquals(
            CharacterFilter(name = "morty", status = CharacterStatus.Alive, gender = Gender.Male),
            repository.requestedFilters.last(),
        )
    }

    @Test
    fun `trailing spaces do not launch a new search`() = runTest {
        val viewModel = observedViewModel()
        viewModel.onQueryChange("morty")
        advanceTimeBy(301)
        val requestsAfterSearch = repository.requestedFilters.size

        viewModel.onQueryChange("morty ")
        advanceTimeBy(301)

        assertEquals(requestsAfterSearch, repository.requestedFilters.size)
        assertEquals("morty ", viewModel.query)
    }

    @Test
    fun `clearing everything goes back to the unfiltered list without waiting`() = runTest {
        val viewModel = observedViewModel()
        viewModel.onQueryChange("morty")
        viewModel.onFiltersApply(CharacterStatus.Alive, Gender.Male)
        advanceTimeBy(301)

        viewModel.onClearFilters()
        runCurrent()

        assertEquals(CharacterFilter(), repository.requestedFilters.last())
        assertEquals("", viewModel.query)
        assertEquals(CharactersUiState(), viewModel.uiState.value)
    }

    @Test
    fun `search and filters survive process death`() = runTest {
        val restored = SavedStateHandle(
            mapOf("query" to "rick", "status" to CharacterStatus.Dead, "gender" to Gender.Male),
        )

        val viewModel = observedViewModel(restored)
        advanceTimeBy(301)

        assertEquals("rick", viewModel.query)
        assertEquals(
            listOf(CharacterFilter(name = "rick", status = CharacterStatus.Dead, gender = Gender.Male)),
            repository.requestedFilters,
        )
    }

    @Test
    fun `the header state follows the filters and the total count`() = runTest {
        val viewModel = observedViewModel()
        runCurrent()

        repository.count.value = 826
        viewModel.onFiltersApply(CharacterStatus.Unknown, Gender.Genderless)
        runCurrent()

        assertEquals(
            CharactersUiState(status = CharacterStatus.Unknown, gender = Gender.Genderless, totalCount = 826),
            viewModel.uiState.value,
        )
    }
}

private class FakeCharacterRepository : CharacterRepository {
    val requestedFilters = mutableListOf<CharacterFilter>()
    val count = MutableStateFlow<Int?>(null)

    override fun observeCharacters(filter: CharacterFilter): Flow<PagingData<Character>> {
        requestedFilters += filter
        return flowOf(PagingData.empty())
    }

    override fun observeCharacterCount(): Flow<Int?> = count

    override suspend fun getCharacter(id: Int): AppResult<Character> = error("not used")
}
