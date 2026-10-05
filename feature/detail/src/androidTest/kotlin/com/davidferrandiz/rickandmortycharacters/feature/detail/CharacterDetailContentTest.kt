package com.davidferrandiz.rickandmortycharacters.feature.detail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.RickAndMortyTheme
import com.davidferrandiz.rickandmortycharacters.domain.error.AppError
import com.davidferrandiz.rickandmortycharacters.domain.model.Character
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterStatus
import com.davidferrandiz.rickandmortycharacters.domain.model.Episode
import com.davidferrandiz.rickandmortycharacters.domain.model.EpisodesState
import com.davidferrandiz.rickandmortycharacters.domain.model.Gender
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CharacterDetailContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val abradolf = Character(
        id = 7,
        name = "Abradolf Lincler",
        status = CharacterStatus.Unknown,
        species = "Human",
        type = "Genetic experiment",
        gender = Gender.Male,
        origin = "Earth (Replacement Dimension)",
        location = "Testicle Monster Dimension",
        imageUrl = "",
        episodeIds = listOf(10, 11),
    )
    private val fiveEpisodes = (1..5).map { Episode(id = it, name = "Episode $it", code = "S01E0$it") }

    private var backPresses = 0
    private var retries = 0

    private fun setContent(uiState: CharacterDetailUiState) {
        composeRule.setContent {
            RickAndMortyTheme {
                CharacterDetailContent(
                    characterId = 7,
                    imageUrl = "",
                    uiState = uiState,
                    onBack = { backPresses++ },
                    onRetry = { retries++ },
                )
            }
        }
    }

    private fun content(
        character: Character = abradolf,
        episodes: EpisodesState = EpisodesState.Loaded(fiveEpisodes),
    ) = CharacterDetailUiState.Content(character, episodes)

    @Test
    fun showsTheCharacterWithItsOptionalType() {
        setContent(content())

        composeRule.onNodeWithText("Abradolf Lincler").assertIsDisplayed()
        composeRule.onNodeWithText("#007").assertIsDisplayed()
        composeRule.onNodeWithText("TYPE").assertExists()
        composeRule.onNodeWithText("Genetic experiment").assertExists()
    }

    @Test
    fun aCharacterWithoutTypeHasNoTypeCardAndUnknownPlacesAreSpelledOut() {
        setContent(content(character = abradolf.copy(type = null, origin = null)))

        composeRule.onNodeWithText("TYPE").assertDoesNotExist()
        composeRule.onNodeWithText("Origin").assertExists()
        composeRule.onNodeWithText("Testicle Monster Dimension").assertExists()
    }

    @Test
    fun episodesStartCollapsedAndExpandInPlace() {
        setContent(content())

        composeRule.onNodeWithText("Episode 3").assertExists()
        composeRule.onNodeWithText("Episode 4").assertDoesNotExist()

        composeRule.onNodeWithText("See all 5 episodes").performScrollTo().performClick()

        composeRule.onNodeWithText("Episode 5").assertExists()
        composeRule.onNodeWithText("Show less").assertExists()
    }

    @Test
    fun aCharacterWithFewEpisodesHasNoToggle() {
        setContent(content(episodes = EpisodesState.Loaded(fiveEpisodes.take(2))))

        composeRule.onNodeWithText("Episode 2").assertExists()
        composeRule.onNodeWithText("See all 2 episodes").assertDoesNotExist()
    }

    @Test
    fun anEpisodesFailureKeepsTheCharacterAndOffersARetry() {
        setContent(content(episodes = EpisodesState.Failed(AppError.Timeout)))

        composeRule.onNodeWithText("Abradolf Lincler").assertIsDisplayed()
        composeRule.onNodeWithText("Retry").performScrollTo().performClick()

        assertEquals(1, retries)
    }

    @Test
    fun aCharacterFailureShowsTheErrorWithItsRetry() {
        setContent(CharacterDetailUiState.Error(AppError.NoConnection))

        composeRule.onNodeWithText("Couldn’t load this character").assertIsDisplayed()
        composeRule.onNodeWithText("Try again").performClick()

        assertEquals(1, retries)
    }

    @Test
    fun theBackButtonReportsBack() {
        setContent(content())

        composeRule.onNodeWithContentDescription("Back to characters").performClick()

        assertEquals(1, backPresses)
    }
}
