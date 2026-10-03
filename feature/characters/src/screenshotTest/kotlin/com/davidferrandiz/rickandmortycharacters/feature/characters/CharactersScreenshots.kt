package com.davidferrandiz.rickandmortycharacters.feature.characters

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.RickAndMortyTheme
import com.davidferrandiz.rickandmortycharacters.domain.error.AppError
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterStatus
import com.davidferrandiz.rickandmortycharacters.domain.model.Gender

@Preview(name = "Light", widthDp = 390, heightDp = 844)
@Preview(name = "Dark", widthDp = 390, heightDp = 844, uiMode = Configuration.UI_MODE_NIGHT_YES)
private annotation class ScreenPreviews

@Composable
private fun Screen(
    query: String = "",
    uiState: CharactersUiState = CharactersUiState(totalCount = 826),
    error: AppError? = null,
    body: @Composable () -> Unit,
) {
    RickAndMortyTheme {
        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            CharactersHeader(
                query = query,
                uiState = uiState,
                error = error,
                onQueryChange = {},
                onStatusSelect = {},
                onOpenFilters = {},
            )
            Box(Modifier.weight(1f)) {
                body()
            }
        }
    }
}

@PreviewTest
@ScreenPreviews
@Composable
fun ListLoading() {
    Screen(uiState = CharactersUiState()) {
        CharactersSkeleton(contentPadding = PaddingValues(start = 20.dp, top = 8.dp, end = 20.dp, bottom = 28.dp))
    }
}

@PreviewTest
@ScreenPreviews
@Composable
fun ListEmptySearch() {
    Screen(
        query = "zara",
        uiState = CharactersUiState(status = CharacterStatus.Alive, gender = Gender.Female, totalCount = 826),
    ) {
        CharactersEmpty(query = "zara", onClearFilters = {})
    }
}

@PreviewTest
@ScreenPreviews
@Composable
fun ListLoadError() {
    Screen(uiState = CharactersUiState(), error = AppError.NoConnection) {
        CharactersError(error = AppError.NoConnection, onRetry = {})
    }
}

@PreviewTest
@PreviewLightDark
@Composable
fun ListFeedback() {
    RickAndMortyTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
        ) {
            OfflineBar(error = AppError.NoConnection, onRetry = {})
            LoadingMoreFooter()
            LoadMoreErrorFooter(onRetry = {})
        }
    }
}

@PreviewTest
@PreviewLightDark
@Composable
fun FiltersSheetDraft() {
    RickAndMortyTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh)) {
            FiltersSheetContent(
                draftStatus = CharacterStatus.Dead,
                draftGender = null,
                onStatusSelect = {},
                onGenderSelect = {},
                onReset = {},
                onApply = {},
            )
        }
    }
}
