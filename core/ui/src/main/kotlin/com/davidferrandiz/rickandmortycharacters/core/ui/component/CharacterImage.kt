package com.davidferrandiz.rickandmortycharacters.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.AppTheme

@Composable
fun CharacterImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalPlatformContext.current
    val request = remember(context, imageUrl) {
        ImageRequest.Builder(context)
            .data(imageUrl)
            .memoryCacheKey(imageUrl)
            .placeholderMemoryCacheKey(imageUrl)
            .build()
    }
    AsyncImage(
        model = request,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier.background(AppTheme.colors.imagePlaceholder),
    )
}
