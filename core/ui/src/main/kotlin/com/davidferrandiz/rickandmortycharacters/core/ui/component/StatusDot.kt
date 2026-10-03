package com.davidferrandiz.rickandmortycharacters.core.ui.component

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davidferrandiz.rickandmortycharacters.core.ui.R
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.AppTheme
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterStatus

@get:StringRes
val CharacterStatus.labelRes: Int
    get() = when (this) {
        CharacterStatus.Alive -> R.string.status_alive
        CharacterStatus.Dead -> R.string.status_dead
        CharacterStatus.Unknown -> R.string.status_unknown
    }

@Composable
fun StatusDot(status: CharacterStatus, modifier: Modifier = Modifier) {
    val color = when (status) {
        CharacterStatus.Alive -> AppTheme.colors.statusAlive
        CharacterStatus.Dead -> AppTheme.colors.statusDead
        CharacterStatus.Unknown -> AppTheme.colors.statusUnknown
    }
    Box(modifier.size(8.dp).background(color, CircleShape))
}
