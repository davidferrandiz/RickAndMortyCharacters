package com.davidferrandiz.rickandmortycharacters.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.Extras
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.AppTheme
import kotlinx.coroutines.delay

private const val MAX_RETRIES = 3
private const val RETRY_BASE_DELAY_MILLIS = 2_000L
private val AttemptKey = Extras.Key(default = 0)

@Composable
fun CharacterImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalPlatformContext.current
    var attempt by remember(imageUrl) { mutableIntStateOf(0) }
    var failed by remember(imageUrl) { mutableStateOf(false) }
    var loaded by remember(imageUrl) { mutableStateOf(false) }
    val request = remember(context, imageUrl, attempt) {
        ImageRequest.Builder(context)
            .data(imageUrl)
            .memoryCacheKey(imageUrl)
            .placeholderMemoryCacheKey(imageUrl)
            .apply { extras[AttemptKey] = attempt }
            .build()
    }
    LaunchedEffect(failed, attempt) {
        if (failed && attempt < MAX_RETRIES) {
            delay(RETRY_BASE_DELAY_MILLIS shl attempt)
            failed = false
            attempt++
        }
    }
    val isWaiting = !loaded && (!failed || attempt < MAX_RETRIES)
    val placeholder = if (isWaiting) {
        Modifier.shimmer(
            progress = rememberShimmerProgress(),
            base = AppTheme.colors.skeletonBase,
            highlight = AppTheme.colors.skeletonHighlight,
        )
    } else {
        Modifier.background(AppTheme.colors.imagePlaceholder)
    }
    AsyncImage(
        model = request,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        onSuccess = { loaded = true },
        onError = { failed = true },
        modifier = modifier.then(placeholder),
    )
}
