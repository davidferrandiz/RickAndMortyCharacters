package com.davidferrandiz.rickandmortycharacters

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import com.davidferrandiz.rickandmortycharacters.image.RateLimitInterceptor
import com.davidferrandiz.rickandmortycharacters.image.SlidingWindowRateLimiter
import dagger.Lazy
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import okhttp3.Dispatcher
import okhttp3.OkHttpClient

private const val IMAGE_CACHE_DIRECTORY = "image_cache"
private const val IMAGE_DISK_CACHE_BYTES = 50L * 1024 * 1024
private const val IMAGE_MEMORY_CACHE_PERCENT = 0.25
private const val IMAGE_REQUESTS_PER_WINDOW = 30
private const val IMAGE_WINDOW_MILLIS = 10_000L

@HiltAndroidApp
class RickAndMortyApp : Application(), SingletonImageLoader.Factory {

    @Inject
    lateinit var okHttpClient: Lazy<OkHttpClient>

    override fun newImageLoader(context: PlatformContext): ImageLoader = ImageLoader.Builder(context)
        .components {
            add(OkHttpNetworkFetcherFactory(callFactory = { imageHttpClient() }))
        }
        .memoryCache {
            MemoryCache.Builder()
                .maxSizePercent(context, IMAGE_MEMORY_CACHE_PERCENT)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(cacheDir.resolve(IMAGE_CACHE_DIRECTORY))
                .maxSizeBytes(IMAGE_DISK_CACHE_BYTES)
                .build()
        }
        .crossfade(true)
        .build()

    private fun imageHttpClient(): OkHttpClient = okHttpClient.get().newBuilder()
        .cache(null)
        .dispatcher(Dispatcher())
        .addInterceptor(
            RateLimitInterceptor(SlidingWindowRateLimiter(IMAGE_REQUESTS_PER_WINDOW, IMAGE_WINDOW_MILLIS)),
        )
        .build()
}
