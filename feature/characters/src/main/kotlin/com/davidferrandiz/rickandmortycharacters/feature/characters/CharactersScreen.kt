package com.davidferrandiz.rickandmortycharacters.feature.characters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.davidferrandiz.rickandmortycharacters.core.ui.component.CharacterCard
import com.davidferrandiz.rickandmortycharacters.domain.model.Character
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterStatus

private val MIN_CARD_WIDTH = 160.dp
private val GRID_SPACING = 12.dp
private const val CHARACTER_CONTENT_TYPE = "character"

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
    onCharacterClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
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
            onQueryChange = onQueryChange,
            onStatusSelect = onStatusSelect,
        )
        CharactersGrid(
            characters = characters,
            onCharacterClick = onCharacterClick,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun CharactersGrid(
    characters: LazyPagingItems<Character>,
    onCharacterClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navigationBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    LazyVerticalGrid(
        columns = GridCells.Adaptive(MIN_CARD_WIDTH),
        horizontalArrangement = Arrangement.spacedBy(GRID_SPACING),
        verticalArrangement = Arrangement.spacedBy(GRID_SPACING),
        contentPadding = PaddingValues(
            start = 20.dp,
            top = 8.dp,
            end = 20.dp,
            bottom = 28.dp + navigationBarPadding,
        ),
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
    }
}
