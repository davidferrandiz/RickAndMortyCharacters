package com.davidferrandiz.rickandmortycharacters.feature.characters

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.davidferrandiz.rickandmortycharacters.core.ui.component.AppChip
import com.davidferrandiz.rickandmortycharacters.core.ui.component.PrimaryButton
import com.davidferrandiz.rickandmortycharacters.core.ui.component.TextAction
import com.davidferrandiz.rickandmortycharacters.core.ui.component.labelRes
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.AppShapes
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.AppTheme
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterStatus
import com.davidferrandiz.rickandmortycharacters.domain.model.Gender
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FiltersSheet(
    appliedStatus: CharacterStatus?,
    appliedGender: Gender?,
    onApply: (CharacterStatus?, Gender?) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var draftStatus by rememberSaveable { mutableStateOf(appliedStatus) }
    var draftGender by rememberSaveable { mutableStateOf(appliedGender) }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = AppShapes.Sheet,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        scrimColor = MaterialTheme.colorScheme.scrim,
        dragHandle = { SheetHandle() },
    ) {
        FiltersSheetContent(
            draftStatus = draftStatus,
            draftGender = draftGender,
            onStatusSelect = { draftStatus = it },
            onGenderSelect = { draftGender = it },
            onReset = {
                draftStatus = null
                draftGender = null
            },
            onApply = {
                scope.launch { sheetState.hide() }.invokeOnCompletion { onApply(draftStatus, draftGender) }
            },
        )
    }
}

@Composable
private fun SheetHandle() {
    Box(
        Modifier
            .padding(top = 12.dp, bottom = 22.dp)
            .size(width = 36.dp, height = 4.dp)
            .background(AppTheme.colors.sheetHandle, RoundedCornerShape(2.dp)),
    )
}

@Composable
internal fun FiltersSheetContent(
    draftStatus: CharacterStatus?,
    draftGender: Gender?,
    onStatusSelect: (CharacterStatus?) -> Unit,
    onGenderSelect: (Gender?) -> Unit,
    onReset: () -> Unit,
    onApply: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(22.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 28.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(R.string.filters_title),
                style = AppTheme.typography.title,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.semantics { heading() },
            )
            TextAction(
                text = stringResource(R.string.filters_reset),
                onClick = onReset,
                underlined = true,
            )
        }
        FilterGroup(
            title = stringResource(R.string.filters_status),
            options = CharacterStatus.entries,
            selected = draftStatus,
            label = { stringResource(it.labelRes) },
            onSelect = onStatusSelect,
        )
        FilterGroup(
            title = stringResource(R.string.filters_gender),
            options = Gender.entries,
            selected = draftGender,
            label = { stringResource(it.labelRes) },
            onSelect = onGenderSelect,
        )
        PrimaryButton(
            text = stringResource(R.string.filters_apply),
            onClick = onApply,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun <T> FilterGroup(
    title: String,
    options: List<T>,
    selected: T?,
    label: @Composable (T) -> String,
    onSelect: (T?) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title.uppercase(),
            style = AppTheme.typography.eyebrow,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AppChip(
                label = stringResource(R.string.filter_any),
                selected = selected == null,
                onClick = { onSelect(null) },
            )
            options.forEach { option ->
                AppChip(
                    label = label(option),
                    selected = selected == option,
                    onClick = { onSelect(option) },
                )
            }
        }
    }
}
