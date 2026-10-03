package com.davidferrandiz.rickandmortycharacters.feature.characters

import androidx.paging.LoadState
import com.davidferrandiz.rickandmortycharacters.domain.error.AppError
import com.davidferrandiz.rickandmortycharacters.domain.error.AppErrorException
import org.junit.Assert.assertEquals
import org.junit.Test

class CharactersListStateTest {

    private val idle = LoadState.NotLoading(endOfPaginationReached = false)
    private val finished = LoadState.NotLoading(endOfPaginationReached = true)
    private val offline = LoadState.Error(AppErrorException(AppError.NoConnection))

    @Test
    fun `before the first load there is a skeleton and not an empty state`() {
        assertEquals(CharactersListState.Loading, charactersListState(refresh = idle, append = idle, itemCount = 0))
    }

    @Test
    fun `a first load in progress shows the skeleton`() {
        assertEquals(
            CharactersListState.Loading,
            charactersListState(refresh = LoadState.Loading, append = idle, itemCount = 0),
        )
    }

    @Test
    fun `a finished load without items is the empty state`() {
        assertEquals(CharactersListState.Empty, charactersListState(refresh = idle, append = finished, itemCount = 0))
    }

    @Test
    fun `a failed first load with nothing cached is the error screen`() {
        assertEquals(
            CharactersListState.Error(AppError.NoConnection),
            charactersListState(refresh = offline, append = idle, itemCount = 0),
        )
    }

    @Test
    fun `a failed refresh with cached items keeps the list and flags the error`() {
        assertEquals(
            CharactersListState.Content(refreshError = AppError.NoConnection),
            charactersListState(refresh = offline, append = idle, itemCount = 20),
        )
    }

    @Test
    fun `items stay visible while a refresh is running`() {
        assertEquals(
            CharactersListState.Content(refreshError = null),
            charactersListState(refresh = LoadState.Loading, append = idle, itemCount = 20),
        )
    }

    @Test
    fun `a failure that is not an AppError still produces an error state`() {
        val state = charactersListState(
            refresh = LoadState.Error(IllegalStateException("boom")),
            append = idle,
            itemCount = 0,
        )

        assertEquals(CharactersListState.Error(AppError.Unknown("boom")), state)
    }
}
