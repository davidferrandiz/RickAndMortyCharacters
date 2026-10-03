package com.davidferrandiz.rickandmortycharacters.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.davidferrandiz.rickandmortycharacters.feature.characters.CharactersScreen
import com.davidferrandiz.rickandmortycharacters.feature.detail.CharacterDetailScreen

@Composable
fun RickAndMortyNavigation(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(CharactersKey)
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<CharactersKey> {
                CharactersScreen(
                    onCharacterClick = { characterId -> backStack.add(CharacterDetailKey(characterId)) },
                )
            }
            entry<CharacterDetailKey> { key ->
                CharacterDetailScreen(
                    characterId = key.characterId,
                    onBack = { backStack.removeLastOrNull() },
                )
            }
        },
        modifier = modifier,
    )
}
