package com.davidferrandiz.rickandmortycharacters.feature.characters

import androidx.paging.LoadState
import com.davidferrandiz.rickandmortycharacters.domain.error.AppError
import com.davidferrandiz.rickandmortycharacters.domain.error.asAppError

internal sealed interface CharactersListState {
    data object Loading : CharactersListState
    data object Empty : CharactersListState
    data class Error(val error: AppError) : CharactersListState
    data class Content(val refreshError: AppError?) : CharactersListState
}

internal fun charactersListState(refresh: LoadState, append: LoadState, itemCount: Int): CharactersListState =
    when {
        itemCount > 0 -> CharactersListState.Content(
            refreshError = (refresh as? LoadState.Error)?.error?.asAppError(),
        )
        refresh is LoadState.Error -> CharactersListState.Error(refresh.error.asAppError())
        refresh is LoadState.NotLoading && append.endOfPaginationReached -> CharactersListState.Empty
        else -> CharactersListState.Loading
    }
