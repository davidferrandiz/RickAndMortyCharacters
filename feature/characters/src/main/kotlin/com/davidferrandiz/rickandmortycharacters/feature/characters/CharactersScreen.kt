package com.davidferrandiz.rickandmortycharacters.feature.characters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.grid.LazyGridState
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
import com.davidferrandiz.rickandmortycharacters.core.ui.transition.characterImageSharedElement
import com.davidferrandiz.rickandmortycharacters.domain.model.Character
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterStatus
import com.davidferrandiz.rickandmortycharacters.domain.model.Gender

internal val MIN_CARD_WIDTH = 160.dp
internal val GRID_SPACING = 12.dp
private val OFFLINE_BAR_CLEARANCE = 88.dp
private val CARD_CORNER_RADIUS = 20.dp
private val COMPACT_HEIGHT = 480.dp
private const val CHARACTER_CONTENT_TYPE = "character"
private const val FOOTER_CONTENT_TYPE = "footer"

@Composable
fun CharactersScreen(
    onCharacterClick: (characterId: Int, imageUrl: String) -> Unit,
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
        onFiltersApply = viewModel::onFiltersApply,
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
    onFiltersApply: (CharacterStatus?, Gender?) -> Unit,
    onClearFilters: () -> Unit,
    onCharacterClick: (characterId: Int, imageUrl: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showFilters by rememberSaveable { mutableStateOf(false) }
    val listState = charactersListState(
        refresh = characters.loadState.refresh,
        append = characters.loadState.append,
        itemCount = characters.itemCount,
    )
    val gridState = rememberSaveable(query.trim(), uiState.status, uiState.gender, saver = LazyGridState.Saver) {
        LazyGridState()
    }
    val header: @Composable (Modifier) -> Unit = { headerModifier ->
        CharactersHeader(
            query = query,
            uiState = uiState,
            error = (listState as? CharactersListState.Error)?.error,
            onQueryChange = onQueryChange,
            onStatusSelect = onStatusSelect,
            onOpenFilters = { showFilters = true },
            modifier = headerModifier,
        )
    }
    val body: @Composable (Dp, Modifier) -> Unit = { topPadding, bodyModifier ->
        CharactersBody(
            listState = listState,
            gridState = gridState,
            characters = characters,
            query = query,
            topPadding = topPadding,
            onClearFilters = onClearFilters,
            onCharacterClick = onCharacterClick,
            modifier = bodyModifier,
        )
    }
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
            ),
    ) {
        if (maxHeight < COMPACT_HEIGHT) {
            CollapsingHeader(header = header) { headerHeight -> body(headerHeight, Modifier.fillMaxSize()) }
        } else {
            Column {
                header(Modifier)
                body(0.dp, Modifier.weight(1f))
            }
        }
    }
    if (showFilters) {
        FiltersSheet(
            appliedStatus = uiState.status,
            appliedGender = uiState.gender,
            onApply = { status, gender ->
                onFiltersApply(status, gender)
                showFilters = false
            },
            onDismiss = { showFilters = false },
        )
    }
}

@Composable
private fun CharactersBody(
    listState: CharactersListState,
    gridState: LazyGridState,
    characters: LazyPagingItems<Character>,
    query: String,
    topPadding: Dp,
    onClearFilters: () -> Unit,
    onCharacterClick: (characterId: Int, imageUrl: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navigationBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Box(modifier = modifier) {
        when (listState) {
            CharactersListState.Loading -> CharactersSkeleton(
                contentPadding = gridPadding(top = topPadding, bottom = navigationBarPadding),
            )
            CharactersListState.Empty -> CharactersEmpty(
                query = query,
                onClearFilters = onClearFilters,
                modifier = Modifier.padding(top = topPadding),
            )
            is CharactersListState.Error -> CharactersError(
                error = listState.error,
                onRetry = characters::retry,
                modifier = Modifier.padding(top = topPadding),
            )
            is CharactersListState.Content -> {
                val refreshError = listState.refreshError
                CharactersGrid(
                    characters = characters,
                    gridState = gridState,
                    contentPadding = gridPadding(
                        top = topPadding,
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

private fun gridPadding(top: Dp, bottom: Dp) =
    PaddingValues(start = 20.dp, top = 8.dp + top, end = 20.dp, bottom = 28.dp + bottom)

@Composable
private fun CharactersGrid(
    characters: LazyPagingItems<Character>,
    gridState: LazyGridState,
    contentPadding: PaddingValues,
    onCharacterClick: (characterId: Int, imageUrl: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(MIN_CARD_WIDTH),
        state = gridState,
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
                    onClick = { onCharacterClick(character.id, character.imageUrl) },
                    imageModifier = Modifier.characterImageSharedElement(
                        characterId = character.id,
                        cornerRadius = CARD_CORNER_RADIUS,
                        counterpartCornerRadius = 0.dp,
                    ),
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
