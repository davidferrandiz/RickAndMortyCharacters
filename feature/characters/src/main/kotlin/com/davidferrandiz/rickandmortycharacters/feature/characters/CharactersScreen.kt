package com.davidferrandiz.rickandmortycharacters.feature.characters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.davidferrandiz.rickandmortycharacters.core.ui.component.CharacterCard
import com.davidferrandiz.rickandmortycharacters.domain.model.Character
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterStatus
import com.davidferrandiz.rickandmortycharacters.domain.model.Gender

internal val MIN_CARD_WIDTH = 160.dp
internal val GRID_SPACING = 12.dp
private val OFFLINE_BAR_CLEARANCE = 88.dp
private const val CHARACTER_CONTENT_TYPE = "character"
private const val FOOTER_CONTENT_TYPE = "footer"

@Composable
fun CharactersScreen(
    onCharacterClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CharactersViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val characters = viewModel.characters.collectAsLazyPagingItems()
    CharactersContent(
        query = viewModel.query,
        uiState = uiState,
        characters = characters,
        onQueryChange = viewModel::onQueryChange,
        onStatusSelect = viewModel::onStatusSelect,
        onGenderApply = viewModel::onGenderApply,
        onClearFilters = viewModel::onClearFilters,
        onCharacterClick = onCharacterClick,
        modifier = modifier,
    )
}

@Composable
internal fun CharactersContent(
    query: String,
    uiState: CharactersUiState,
    characters: LazyPagingItems<Character>,
    onQueryChange: (String) -> Unit,
    onStatusSelect: (CharacterStatus?) -> Unit,
    onGenderApply: (Gender?) -> Unit,
    onClearFilters: () -> Unit,
    onCharacterClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showFilters by rememberSaveable { mutableStateOf(false) }
    val listState = charactersListState(
        refresh = characters.loadState.refresh,
        append = characters.loadState.append,
        itemCount = characters.itemCount,
    )
    val navigationBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
            ),
    ) {
        CharactersHeader(
            query = query,
            uiState = uiState,
            error = (listState as? CharactersListState.Error)?.error,
            onQueryChange = onQueryChange,
            onStatusSelect = onStatusSelect,
            onOpenFilters = { showFilters = true },
        )
        Box(modifier = Modifier.weight(1f)) {
            when (listState) {
                CharactersListState.Loading -> CharactersSkeleton(
                    contentPadding = gridPadding(bottom = navigationBarPadding),
                )
                CharactersListState.Empty -> CharactersEmpty(
                    query = query,
                    onClearFilters = onClearFilters,
                )
                is CharactersListState.Error -> CharactersError(
                    error = listState.error,
                    onRetry = characters::retry,
                )
                is CharactersListState.Content -> {
                    val refreshError = listState.refreshError
                    CharactersGrid(
                        characters = characters,
                        contentPadding = gridPadding(
                            bottom = navigationBarPadding + if (refreshError != null) OFFLINE_BAR_CLEARANCE else 0.dp,
                        ),
                        onCharacterClick = onCharacterClick,
                    )
                    if (refreshError != null) {
                        OfflineBar(
                            error = refreshError,
                            onRetry = characters::retry,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(start = 16.dp, end = 16.dp, bottom = 24.dp + navigationBarPadding),
                        )
                    }
                }
            }
        }
    }
    if (showFilters) {
        FiltersSheet(
            appliedGender = uiState.gender,
            onApply = { gender ->
                onGenderApply(gender)
                showFilters = false
            },
            onDismiss = { showFilters = false },
        )
    }
}

private fun gridPadding(bottom: Dp) = PaddingValues(start = 20.dp, top = 8.dp, end = 20.dp, bottom = 28.dp + bottom)

@Composable
private fun CharactersGrid(
    characters: LazyPagingItems<Character>,
    contentPadding: PaddingValues,
    onCharacterClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(MIN_CARD_WIDTH),
        horizontalArrangement = Arrangement.spacedBy(GRID_SPACING),
        verticalArrangement = Arrangement.spacedBy(GRID_SPACING),
        contentPadding = contentPadding,
        modifier = modifier,
    ) {
        items(
            count = characters.itemCount,
            key = characters.itemKey(Character::id),
            contentType = characters.itemContentType { CHARACTER_CONTENT_TYPE },
        ) { index ->
            characters[index]?.let { character ->
                CharacterCard(
                    id = character.id,
                    name = character.name,
                    status = character.status,
                    species = character.species,
                    imageUrl = character.imageUrl,
                    onClick = { onCharacterClick(character.id) },
                )
            }
        }
        when (characters.loadState.append) {
            is LoadState.Loading -> item(
                span = { GridItemSpan(maxLineSpan) },
                contentType = FOOTER_CONTENT_TYPE,
            ) { LoadingMoreFooter() }
            is LoadState.Error -> item(
                span = { GridItemSpan(maxLineSpan) },
                contentType = FOOTER_CONTENT_TYPE,
            ) { LoadMoreErrorFooter(onRetry = characters::retry) }
            is LoadState.NotLoading -> Unit
        }
    }
}
