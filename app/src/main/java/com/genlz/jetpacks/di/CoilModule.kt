package com.genlz.jetpacks.di

import android.content.Context
import coil.Coil
import coil.ImageLoader
import coil.util.CoilUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoilModule {

    @Singleton
    @Provides
    fun provideImageLoader(
        @ApplicationContext context: Context,
    ): ImageLoader {
        return ImageLoader.Builder(context)
            .okHttpClient {
                // Create a disk cache with "unlimited" size. Don't do this in production.
                // To create the an optimized Coil disk cache, use CoilUtils.createDefaultCache(context).
                val cache = CoilUtils.createDefaultCache(context)
                // Rewrite the Cache-Control header to cache all responses for a year.

                // Don't limit concurrent network requests by host.
                val dispatcher = Dispatcher().apply { maxRequestsPerHost = maxRequests }

                // Lazily create the OkHttpClient that is used for network operations.
                OkHttpClient.Builder()
                    .cache(cache)
                    .dispatcher(dispatcher)
                    .build()
            }
            .build().also(Coil::setImageLoader)
    }
}