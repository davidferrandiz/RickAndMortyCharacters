package com.davidferrandiz.rickandmortycharacters.feature.characters

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.RickAndMortyTheme
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterStatus
import com.davidferrandiz.rickandmortycharacters.domain.model.Gender
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FiltersSheetTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val applied = mutableListOf<Pair<CharacterStatus?, Gender?>>()

    private fun setContent(appliedStatus: CharacterStatus? = null, appliedGender: Gender? = null) {
        composeRule.setContent {
            RickAndMortyTheme {
                FiltersSheet(
                    appliedStatus = appliedStatus,
                    appliedGender = appliedGender,
                    onApply = { status, gender -> applied += status to gender },
                    onDismiss = {},
                )
            }
        }
    }

    @Test
    fun choosingChipsDoesNotApplyUntilTheButtonIsPressed() {
        setContent()

        composeRule.onNodeWithText("Dead").performClick()
        composeRule.onNodeWithText("Female").performClick()
        composeRule.waitForIdle()

        assertTrue(applied.isEmpty())

        composeRule.onNodeWithText("Apply filters").performClick()
        composeRule.waitForIdle()

        assertEquals(listOf<Pair<CharacterStatus?, Gender?>>(CharacterStatus.Dead to Gender.Female), applied)
    }

    @Test
    fun theSheetOpensWithWhatIsAlreadyApplied() {
        setContent(appliedStatus = CharacterStatus.Alive, appliedGender = Gender.Male)

        composeRule.onNodeWithText("Apply filters").performClick()
        composeRule.waitForIdle()

        assertEquals(listOf<Pair<CharacterStatus?, Gender?>>(CharacterStatus.Alive to Gender.Male), applied)
    }

    @Test
    fun resetClearsTheDraftAndApplyingSendsNoFilters() {
        setContent(appliedStatus = CharacterStatus.Alive, appliedGender = Gender.Male)

        composeRule.onNodeWithText("Reset").performClick()
        composeRule.onNodeWithText("Apply filters").performClick()
        composeRule.waitForIdle()

        assertEquals(listOf<Pair<CharacterStatus?, Gender?>>(null to null), applied)
    }
}
