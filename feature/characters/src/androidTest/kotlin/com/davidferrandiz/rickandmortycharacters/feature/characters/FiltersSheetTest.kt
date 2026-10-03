package com.davidferrandiz.rickandmortycharacters.feature.characters

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.RickAndMortyTheme
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

    private val applied = mutableListOf<Gender?>()

    private fun setContent(appliedGender: Gender?) {
        composeRule.setContent {
            RickAndMortyTheme {
                FiltersSheet(
                    appliedGender = appliedGender,
                    onApply = { applied += it },
                    onDismiss = {},
                )
            }
        }
    }

    @Test
    fun choosingAChipDoesNotApplyUntilTheButtonIsPressed() {
        setContent(appliedGender = null)

        composeRule.onNodeWithText("Female").performClick()
        composeRule.waitForIdle()

        assertTrue(applied.isEmpty())

        composeRule.onNodeWithText("Apply filters").performClick()
        composeRule.waitForIdle()

        assertEquals(listOf<Gender?>(Gender.Female), applied)
    }

    @Test
    fun resetClearsTheDraftAndApplyingSendsNoGender() {
        setContent(appliedGender = Gender.Male)

        composeRule.onNodeWithText("Reset").performClick()
        composeRule.onNodeWithText("Apply filters").performClick()
        composeRule.waitForIdle()

        assertEquals(listOf<Gender?>(null), applied)
    }
}
