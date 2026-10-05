package com.davidferrandiz.rickandmortycharacters.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.AppTheme
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterStatus

@Composable
fun StatusDot(status: CharacterStatus, modifier: Modifier = Modifier) {
    val color = when (status) {
        CharacterStatus.Alive -> AppTheme.colors.statusAlive
        CharacterStatus.Dead -> AppTheme.colors.statusDead
        CharacterStatus.Unknown -> AppTheme.colors.statusUnknown
    }
    Box(modifier.size(8.dp).background(color, CircleShape))
}
