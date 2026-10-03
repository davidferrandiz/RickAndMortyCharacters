package com.davidferrandiz.rickandmortycharacters.data.remote

import android.util.Log
import com.davidferrandiz.rickandmortycharacters.domain.common.AppResult
import com.davidferrandiz.rickandmortycharacters.domain.error.AppError
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException

private const val TAG = "RickAndMortyNetwork"

internal suspend fun <T> safeApiCall(call: suspend () -> T): AppResult<T> =
    try {
        AppResult.Success(call())
    } catch (e: CancellationException) {
        throw e
    } catch (e: SocketTimeoutException) {
        Log.w(TAG, "Timeout", e)
        AppResult.Error(AppError.Timeout)
    } catch (e: IOException) {
        Log.w(TAG, "No connection", e)
        AppResult.Error(AppError.NoConnection)
    } catch (e: HttpException) {
        Log.w(TAG, "HTTP ${e.code()} at ${e.response()?.raw()?.request?.url}", e)
        AppResult.Error(AppError.Http(e.code()))
    } catch (e: SerializationException) {
        Log.w(TAG, "Parsing error", e)
        AppResult.Error(AppError.Serialization(e.message))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error", e)
        AppResult.Error(AppError.Unknown(e.message))
    }
