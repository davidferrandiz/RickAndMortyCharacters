package com.davidferrandiz.rickandmortycharacters.feature.characters

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.davidferrandiz.rickandmortycharacters.core.ui.component.AppChip
import com.davidferrandiz.rickandmortycharacters.core.ui.component.SearchField
import com.davidferrandiz.rickandmortycharacters.core.ui.component.labelRes
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.AppTheme
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterStatus
import com.davidferrandiz.rickandmortycharacters.domain.model.Gender
import com.davidferrandiz.rickandmortycharacters.core.ui.R as CoreUiR

@Composable
internal fun CharactersHeader(
    query: String,
    uiState: CharactersUiState,
    onQueryChange: (String) -> Unit,
    onStatusSelect: (CharacterStatus?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.padding(top = 16.dp, bottom = 12.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 20.dp),
        ) {
            Text(
                text = eyebrow(query, uiState).uppercase(),
                style = AppTheme.typography.eyebrow,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
            Text(
                text = stringResource(R.string.characters_title),
                style = AppTheme.typography.screenTitle,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        SearchField(
            query = query,
            onQueryChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            AppChip(
                label = stringResource(R.string.filter_all),
                selected = uiState.status == null,
                onClick = { onStatusSelect(null) },
            )
            CharacterStatus.entries.forEach { status ->
                AppChip(
                    label = stringResource(status.labelRes),
                    selected = uiState.status == status,
                    onClick = { onStatusSelect(status) },
                )
            }
        }
    }
}

@Composable
private fun eyebrow(query: String, uiState: CharactersUiState): String {
    val active = buildList {
        if (query.isNotBlank()) add(stringResource(R.string.summary_query, query.trim()))
        uiState.status?.let { add(stringResource(it.summaryRes)) }
        uiState.gender?.let { add(stringResource(it.summaryRes)) }
    }
    val totalCount = uiState.totalCount
    return when {
        active.isNotEmpty() -> active.joinToString(stringResource(R.string.summary_separator))
        totalCount != null -> pluralStringResource(R.plurals.characters_count, totalCount, totalCount)
        else -> stringResource(R.string.characters_loading)
    }
}

private val CharacterStatus.summaryRes: Int
    get() = when (this) {
        CharacterStatus.Unknown -> R.string.summary_status_unknown
        else -> labelRes
    }

internal val Gender.labelRes: Int
    get() = when (this) {
        Gender.Female -> R.string.gender_female
        Gender.Male -> R.string.gender_male
        Gender.Genderless -> R.string.gender_genderless
        Gender.Unknown -> CoreUiR.string.status_unknown
    }

private val Gender.summaryRes: Int
    get() = when (this) {
        Gender.Unknown -> R.string.summary_gender_unknown
        else -> labelRes
    }
