package com.davidferrandiz.rickandmortycharacters.feature.characters

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

@Composable
fun CharactersScreen(
    onCharacterClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(20.dp),
    ) {
        Text(
            text = stringResource(R.string.characters_title),
            style = AppTheme.typography.screenTitle,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}
