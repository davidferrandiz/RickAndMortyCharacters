package com.davidferrandiz.rickandmortycharacters.data.remote

import java.io.IOException
import java.util.concurrent.TimeUnit
import okhttp3.Interceptor
import okhttp3.Response

private const val HTTP_TOO_MANY_REQUESTS = 429
private const val RETRY_AFTER_HEADER = "Retry-After"
private const val MAX_RETRY_AFTER_SECONDS = 30L
private const val MAX_ATTEMPTS = 3
private const val POLL_MILLIS = 250L

internal class RateLimitInterceptor(
    private val limiter: SlidingWindowRateLimiter,
    private val backoff: ServerBackoff,
    private val now: () -> Long = System::currentTimeMillis,
    private val sleep: (millis: Long) -> Unit = Thread::sleep,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var response = proceedWhenAllowed(chain)
        var attempt = 1
        while (attempt < MAX_ATTEMPTS) {
            val retryAfterMillis = response.retryAfterMillis() ?: return response
            backoff.block(now(), retryAfterMillis)
            response.close()
            response = proceedWhenAllowed(chain)
            attempt++
        }
        return response
    }

    private fun proceedWhenAllowed(chain: Interceptor.Chain): Response {
        while (true) {
            if (chain.call().isCanceled()) throw IOException("Canceled")
            val blockedMillis = backoff.millisUntilOpen(now())
            val waitMillis = if (blockedMillis > 0L) blockedMillis else limiter.millisUntilGranted()
            if (waitMillis == 0L) return chain.proceed(chain.request())
            sleep(minOf(waitMillis, POLL_MILLIS))
        }
    }

    private fun Response.retryAfterMillis(): Long? =
        header(RETRY_AFTER_HEADER)
            ?.toLongOrNull()
            ?.takeIf { code == HTTP_TOO_MANY_REQUESTS && it in 0..MAX_RETRY_AFTER_SECONDS }
            ?.let(TimeUnit.SECONDS::toMillis)
}
