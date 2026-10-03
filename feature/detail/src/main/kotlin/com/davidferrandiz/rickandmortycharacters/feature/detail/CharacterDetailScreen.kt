package com.davidferrandiz.rickandmortycharacters.feature.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.AppTheme
import com.davidferrandiz.rickandmortycharacters.core.ui.R as CoreUiR

@Composable
fun CharacterDetailScreen(
    characterId: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(20.dp),
    ) {
        Text(
            text = stringResource(CoreUiR.string.character_code, characterId),
            style = AppTheme.typography.detailName,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}
