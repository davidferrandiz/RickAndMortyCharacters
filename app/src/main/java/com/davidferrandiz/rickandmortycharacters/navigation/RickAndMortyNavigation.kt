package com.davidferrandiz.rickandmortycharacters.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import com.davidferrandiz.rickandmortycharacters.core.ui.transition.LocalAnimatedVisibilityScope
import com.davidferrandiz.rickandmortycharacters.core.ui.transition.LocalSharedTransitionScope
import com.davidferrandiz.rickandmortycharacters.core.ui.transition.transitionSpec
import com.davidferrandiz.rickandmortycharacters.feature.characters.CharactersScreen
import com.davidferrandiz.rickandmortycharacters.feature.detail.CharacterDetailScreen

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RickAndMortyNavigation(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(CharactersKey)
    SharedTransitionLayout(modifier = modifier) {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
                transitionSpec = { crossFade() },
                popTransitionSpec = { crossFade() },
                predictivePopTransitionSpec = { crossFade() },
                entryProvider = entryProvider {
                    entry<CharactersKey> {
                        WithTransitionScope {
                            CharactersScreen(
                                onCharacterClick = { characterId, imageUrl ->
                                    backStack.add(CharacterDetailKey(characterId, imageUrl))
                                },
                            )
                        }
                    }
                    entry<CharacterDetailKey> { key ->
                        WithTransitionScope {
                            CharacterDetailScreen(
                                characterId = key.characterId,
                                imageUrl = key.imageUrl,
                                onBack = { backStack.removeLastOrNull() },
                            )
                        }
                    }
                },
            )
        }
    }
}

@Composable
private fun WithTransitionScope(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalAnimatedVisibilityScope provides LocalNavAnimatedContentScope.current,
        content = content,
    )
}

private fun crossFade(): ContentTransform = fadeIn(transitionSpec()) togetherWith fadeOut(transitionSpec())
