package com.davidferrandiz.rickandmortycharacters.core.ui.component

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay

private const val MAX_RETRIES = 3
private const val RETRY_BASE_DELAY_MILLIS = 2_000L

@Stable
internal class RetryState(
    private val maxRetries: Int = MAX_RETRIES,
    private val baseDelayMillis: Long = RETRY_BASE_DELAY_MILLIS,
) {
    var attempt by mutableIntStateOf(0)
        private set

    private var failed by mutableStateOf(false)
    private var loaded by mutableStateOf(false)
    private val failures = Channel<Unit>(Channel.CONFLATED)

    val isWaiting: Boolean
        get() = !loaded && (!failed || attempt < maxRetries)

    fun onSuccess() {
        loaded = true
    }

    fun onError() {
        failed = true
        failures.trySend(Unit)
    }

    suspend fun retryFailures() {
        for (failure in failures) {
            if (attempt >= maxRetries) continue
            delay(baseDelayMillis shl attempt)
            failed = false
            attempt++
        }
    }
}
