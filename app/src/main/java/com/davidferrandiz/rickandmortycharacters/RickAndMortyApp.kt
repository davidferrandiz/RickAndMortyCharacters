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
import com.davidferrandiz.rickandmortycharacters.data.di.ImageHttpClient
import dagger.Lazy
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import okhttp3.OkHttpClient

private const val IMAGE_CACHE_DIRECTORY = "image_cache"
private const val IMAGE_DISK_CACHE_BYTES = 50L * 1024 * 1024
private const val IMAGE_MEMORY_CACHE_PERCENT = 0.25

@HiltAndroidApp
class RickAndMortyApp : Application(), SingletonImageLoader.Factory {

    @Inject
    @field:ImageHttpClient
    lateinit var imageHttpClient: Lazy<OkHttpClient>

    override fun newImageLoader(context: PlatformContext): ImageLoader = ImageLoader.Builder(context)
        .components {
            add(OkHttpNetworkFetcherFactory(callFactory = { imageHttpClient.get() }))
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
}
