package com.davidferrandiz.rickandmortycharacters.core.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

object AppShapes {
    val Card = RoundedCornerShape(20.dp)
    val Field = RoundedCornerShape(16.dp)
    val Panel = RoundedCornerShape(16.dp)
    val Sheet = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val Pill = CircleShape
}
