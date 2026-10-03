package com.davidferrandiz.rickandmortycharacters.data.di

import android.content.Context
import com.davidferrandiz.rickandmortycharacters.data.remote.RateLimitInterceptor
import com.davidferrandiz.rickandmortycharacters.data.remote.RickAndMortyApi
import com.davidferrandiz.rickandmortycharacters.data.remote.ServerBackoff
import com.davidferrandiz.rickandmortycharacters.data.remote.SlidingWindowRateLimiter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.Dispatcher
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

private const val BASE_URL = "https://rickandmortyapi.com/api/"
private const val HTTP_CACHE_DIRECTORY = "http_cache"
private const val HTTP_CACHE_SIZE_BYTES = 10L * 1024 * 1024
private const val RATE_WINDOW_MILLIS = 10_000L
private const val API_REQUESTS_PER_WINDOW = 8
private const val IMAGE_REQUESTS_PER_WINDOW = 28

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideHttpCache(@ApplicationContext context: Context): Cache =
        Cache(File(context.cacheDir, HTTP_CACHE_DIRECTORY), HTTP_CACHE_SIZE_BYTES)

    @Provides
    @Singleton
    fun provideOkHttpClient(cache: Cache, backoff: ServerBackoff): OkHttpClient = OkHttpClient.Builder()
        .cache(cache)
        .addInterceptor(rateLimit(API_REQUESTS_PER_WINDOW, backoff))
        .build()

    @Provides
    @Singleton
    @ImageHttpClient
    fun provideImageHttpClient(client: OkHttpClient, backoff: ServerBackoff): OkHttpClient = client.newBuilder()
        .cache(null)
        .dispatcher(Dispatcher())
        .apply { interceptors().clear() }
        .addInterceptor(rateLimit(IMAGE_REQUESTS_PER_WINDOW, backoff))
        .build()

    private fun rateLimit(requestsPerWindow: Int, backoff: ServerBackoff) =
        RateLimitInterceptor(SlidingWindowRateLimiter(requestsPerWindow, RATE_WINDOW_MILLIS), backoff)

    @Provides
    @Singleton
    fun provideRetrofit(json: Json, client: OkHttpClient): Retrofit =
        retrofit(BASE_URL.toHttpUrl(), json, client)

    fun retrofit(baseUrl: HttpUrl, json: Json, client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun provideRickAndMortyApi(retrofit: Retrofit): RickAndMortyApi =
        retrofit.create(RickAndMortyApi::class.java)
}
