package com.genlz.jetpacks.datasource

import android.content.Context
import android.graphics.drawable.Drawable
import coil.ImageLoader
import coil.request.ImageRequest
import com.genlz.jetpacks.di.IoDispatcher
import com.genlz.jetpacks.di.NetworkModule
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SplashDataSource @Inject constructor(
    @IoDispatcher
    private val dispatcher: CoroutineDispatcher,
    private val imageLoader: ImageLoader,
    @ApplicationContext
    private val context: Context,
) {

    suspend fun getRandomSplash(): Drawable? {
        return withContext(dispatcher) {
            val imgRequest = ImageRequest.Builder(context)
                .data(SPLASH_RANDOM_URL)
                .size(1080, 1920)
                .crossfade(true)
                .build()
            val result = imageLoader.execute(imgRequest)
            result.drawable
        }
    }

    companion object {

        const val SPLASH_RANDOM_URL = NetworkModule.SPLASH_BASE_URL + "random"
    }
}