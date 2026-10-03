package com.davidferrandiz.rickandmortycharacters.feature.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidferrandiz.rickandmortycharacters.domain.usecase.GetCharacterDetailUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

private const val STATE_TIMEOUT_MILLIS = 5_000L

@HiltViewModel(assistedFactory = CharacterDetailViewModel.Factory::class)
class CharacterDetailViewModel @AssistedInject constructor(
    @Assisted private val characterId: Int,
    private val getCharacterDetail: GetCharacterDetailUseCase,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(characterId: Int): CharacterDetailViewModel
    }

    private val retryTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<CharacterDetailUiState> = retryTrigger
        .onStart { emit(Unit) }
        .flatMapLatest {
            getCharacterDetail(characterId)
                .map { result -> result.toUiState() }
                .onStart { emit(CharacterDetailUiState.Loading) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STATE_TIMEOUT_MILLIS),
            initialValue = CharacterDetailUiState.Loading,
        )

    fun onRetry() {
        retryTrigger.tryEmit(Unit)
    }
}
