package com.davidferrandiz.rickandmortycharacters.feature.characters

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.davidferrandiz.rickandmortycharacters.domain.model.Character
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterFilter
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterStatus
import com.davidferrandiz.rickandmortycharacters.domain.model.Gender
import com.davidferrandiz.rickandmortycharacters.domain.repository.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

private const val KEY_QUERY = "query"
private const val KEY_STATUS = "status"
private const val KEY_GENDER = "gender"
private const val SEARCH_DEBOUNCE_MILLIS = 300L
private const val STATE_TIMEOUT_MILLIS = 5_000L

@HiltViewModel
class CharactersViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    repository: CharacterRepository,
) : ViewModel() {

    var query: String by mutableStateOf(savedStateHandle[KEY_QUERY] ?: "")
        private set

    private val savedQuery = savedStateHandle.getStateFlow(KEY_QUERY, "")
    private val status = savedStateHandle.getStateFlow<CharacterStatus?>(KEY_STATUS, null)
    private val gender = savedStateHandle.getStateFlow<Gender?>(KEY_GENDER, null)

    val uiState: StateFlow<CharactersUiState> =
        combine(status, gender, repository.observeCharacterCount(), ::CharactersUiState)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STATE_TIMEOUT_MILLIS),
                initialValue = CharactersUiState(status = status.value, gender = gender.value),
            )

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val characters: Flow<PagingData<Character>> =
        combine(
            savedQuery.debounce { text -> if (text.isBlank()) 0L else SEARCH_DEBOUNCE_MILLIS },
            status,
            gender,
        ) { text, status, gender -> CharacterFilter(name = text.trim(), status = status, gender = gender) }
            .distinctUntilChanged()
            .flatMapLatest(repository::observeCharacters)
            .cachedIn(viewModelScope)

    fun onQueryChange(text: String) {
        query = text
        savedStateHandle[KEY_QUERY] = text
    }

    fun onStatusSelect(status: CharacterStatus?) {
        savedStateHandle[KEY_STATUS] = status
    }

    fun onFiltersApply(status: CharacterStatus?, gender: Gender?) {
        savedStateHandle[KEY_STATUS] = status
        savedStateHandle[KEY_GENDER] = gender
    }

    fun onClearFilters() {
        onQueryChange("")
        onFiltersApply(status = null, gender = null)
    }
}
