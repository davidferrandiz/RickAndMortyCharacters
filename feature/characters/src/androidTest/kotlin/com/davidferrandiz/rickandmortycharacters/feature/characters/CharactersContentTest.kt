package com.davidferrandiz.rickandmortycharacters.feature.characters

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.RickAndMortyTheme
import com.davidferrandiz.rickandmortycharacters.domain.error.AppError
import com.davidferrandiz.rickandmortycharacters.domain.error.AppErrorException
import com.davidferrandiz.rickandmortycharacters.domain.model.Character
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterStatus
import com.davidferrandiz.rickandmortycharacters.domain.model.Gender
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CharactersContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val rick = character(id = 1, name = "Rick Sanchez", status = CharacterStatus.Alive)
    private val morty = character(id = 2, name = "Morty Smith", status = CharacterStatus.Alive)

    private val idle = LoadState.NotLoading(endOfPaginationReached = false)
    private val finished = LoadState.NotLoading(endOfPaginationReached = true)
    private val offline = LoadState.Error(AppErrorException(AppError.NoConnection))

    private var clickedCharacterId: Int? = null
    private var selectedStatus: CharacterStatus? = null
    private var filtersCleared = false

    private fun setContent(
        characters: List<Character>,
        refresh: LoadState = idle,
        append: LoadState = finished,
        query: String = "",
        uiState: CharactersUiState = CharactersUiState(totalCount = 826),
    ) {
        val pagingData = PagingData.from(
            data = characters,
            sourceLoadStates = LoadStates(refresh = refresh, prepend = finished, append = append),
        )
        composeRule.setContent {
            RickAndMortyTheme {
                CharactersContent(
                    query = query,
                    uiState = uiState,
                    characters = flowOf(pagingData).collectAsLazyPagingItems(),
                    onQueryChange = {},
                    onStatusSelect = { selectedStatus = it },
                    onGenderApply = {},
                    onClearFilters = { filtersCleared = true },
                    onCharacterClick = { characterId, _ -> clickedCharacterId = characterId },
                )
            }
        }
    }

    @Test
    fun tappingACardReportsThatCharacter() {
        setContent(characters = listOf(rick, morty))

        composeRule.onNodeWithText("Morty Smith").performClick()

        assertEquals(2, clickedCharacterId)
    }

    @Test
    fun theHeaderShowsTheTotalWhenNothingIsFiltered() {
        setContent(characters = listOf(rick))

        composeRule.onNodeWithText("826 CHARACTERS").assertIsDisplayed()
    }

    @Test
    fun theHeaderSummarisesTheActiveFiltersInsteadOfACount() {
        setContent(
            characters = listOf(morty),
            query = "morty",
            uiState = CharactersUiState(status = CharacterStatus.Unknown, gender = Gender.Unknown, totalCount = 826),
        )

        composeRule.onNodeWithText("“MORTY” · STATUS UNKNOWN · GENDER UNKNOWN").assertIsDisplayed()
    }

    @Test
    fun aStatusChipReportsTheSelection() {
        setContent(characters = listOf(rick))

        composeRule.onNodeWithText("Dead").performClick()

        assertEquals(CharacterStatus.Dead, selectedStatus)
    }

    @Test
    fun aSearchWithoutMatchesShowsTheEmptyMessageAndCanBeCleared() {
        setContent(characters = emptyList(), query = "zara")

        composeRule.onNodeWithText("No characters named “zara”").assertIsDisplayed()
        composeRule.onNodeWithText("Clear search and filters").performClick()

        assertTrue(filtersCleared)
    }

    @Test
    fun aFailedFirstLoadShowsTheErrorScreen() {
        setContent(characters = emptyList(), refresh = offline, append = idle)

        composeRule.onNodeWithText("Couldn’t load characters").assertIsDisplayed()
        composeRule.onNodeWithText("Try again").assertIsDisplayed()
        composeRule.onNodeWithText("NO CONNECTION").assertIsDisplayed()
    }

    @Test
    fun aFailedRefreshWithCachedCharactersKeepsTheListAndShowsTheOfflineBar() {
        setContent(characters = listOf(rick, morty), refresh = offline, append = idle)

        composeRule.onNodeWithText("Rick Sanchez").assertIsDisplayed()
        composeRule.onNodeWithText("Showing saved characters").assertIsDisplayed()
        composeRule.onNodeWithText("Retry").assertIsDisplayed()
    }
}

private fun character(id: Int, name: String, status: CharacterStatus) = Character(
    id = id,
    name = name,
    status = status,
    species = "Human",
    type = null,
    gender = Gender.Male,
    origin = null,
    location = null,
    imageUrl = "",
    episodeIds = emptyList(),
)
