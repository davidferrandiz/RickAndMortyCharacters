package com.davidferrandiz.rickandmortycharacters.data.remote

import java.io.IOException
import java.util.concurrent.TimeUnit
import okhttp3.Interceptor
import okhttp3.Response

private const val HTTP_TOO_MANY_REQUESTS = 429
private const val RETRY_AFTER_HEADER = "Retry-After"
private const val MAX_RETRY_AFTER_SECONDS = 10L

internal class RetryAfterInterceptor(
    private val sleep: (millis: Long) -> Unit = Thread::sleep,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val retryAfterSeconds = response.retryAfterSeconds() ?: return response
        response.close()
        sleep(TimeUnit.SECONDS.toMillis(retryAfterSeconds))
        if (chain.call().isCanceled()) throw IOException("Canceled")
        return chain.proceed(chain.request())
    }

    private fun Response.retryAfterSeconds(): Long? =
        header(RETRY_AFTER_HEADER)
            ?.toLongOrNull()
            ?.takeIf { code == HTTP_TOO_MANY_REQUESTS && it in 0..MAX_RETRY_AFTER_SECONDS }
}
