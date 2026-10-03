package com.davidferrandiz.rickandmortycharacters.image

import java.io.IOException
import okhttp3.Interceptor
import okhttp3.Response

private const val POLL_MILLIS = 250L

internal class RateLimitInterceptor(
    private val limiter: SlidingWindowRateLimiter,
    private val sleep: (millis: Long) -> Unit = Thread::sleep,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        while (true) {
            if (chain.call().isCanceled()) throw IOException("Canceled")
            val wait = limiter.millisUntilGranted()
            if (wait == 0L) return chain.proceed(chain.request())
            sleep(minOf(wait, POLL_MILLIS))
        }
    }
}
