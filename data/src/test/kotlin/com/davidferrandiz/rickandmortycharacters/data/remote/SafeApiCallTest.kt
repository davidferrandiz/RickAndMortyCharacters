package com.davidferrandiz.rickandmortycharacters.data.remote

import com.davidferrandiz.rickandmortycharacters.domain.common.AppResult
import com.davidferrandiz.rickandmortycharacters.domain.error.AppError
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class SafeApiCallTest {

    @Test
    fun `wraps the value in Success when the call returns`() = runTest {
        val result = safeApiCall { "payload" }

        assertEquals(AppResult.Success("payload"), result)
    }

    @Test
    fun `maps SocketTimeoutException to Timeout and not to NoConnection`() = runTest {
        val result = safeApiCall<Unit> { throw SocketTimeoutException() }

        assertEquals(AppError.Timeout, result.errorOrFail())
    }

    @Test
    fun `maps a generic IOException to NoConnection`() = runTest {
        val result = safeApiCall<Unit> { throw IOException("network is unreachable") }

        assertEquals(AppError.NoConnection, result.errorOrFail())
    }

    @Test
    fun `maps UnknownHostException to NoConnection`() = runTest {
        val result = safeApiCall<Unit> { throw UnknownHostException("rickandmortyapi.com") }

        assertEquals(AppError.NoConnection, result.errorOrFail())
    }

    @Test
    fun `maps HttpException keeping the status code`() = runTest {
        val result = safeApiCall<Unit> { throw httpException(code = 429) }

        assertEquals(AppError.Http(429), result.errorOrFail())
    }

    @Test
    fun `maps SerializationException keeping the detail`() = runTest {
        val result = safeApiCall<Unit> { throw SerializationException("id is not an Int") }

        assertEquals(AppError.Serialization("id is not an Int"), result.errorOrFail())
    }

    @Test
    fun `maps any other exception to Unknown`() = runTest {
        val result = safeApiCall<Unit> { throw IllegalStateException("boom") }

        assertEquals(AppError.Unknown("boom"), result.errorOrFail())
    }

    @Test
    fun `rethrows CancellationException instead of turning it into an error`() = runTest {
        var rethrown = false

        try {
            safeApiCall<Unit> { throw CancellationException("collector is gone") }
        } catch (e: CancellationException) {
            rethrown = true
        }

        assertTrue(rethrown)
    }
}

private fun httpException(code: Int) = HttpException(
    Response.error<Unit>(code, "".toResponseBody("application/json".toMediaType())),
)

private fun AppResult<*>.errorOrFail(): AppError {
    assertTrue("Expected Error but was $this", this is AppResult.Error)
    return (this as AppResult.Error).error
}
