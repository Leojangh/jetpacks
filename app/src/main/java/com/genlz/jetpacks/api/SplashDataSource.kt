package com.genlz.jetpacks.api

import android.content.Context
import android.graphics.drawable.Drawable
import coil.ImageLoader
import coil.request.ImageRequest
import com.genlz.jetpacks.di.IoDispatcher
import com.genlz.jetpacks.di.RetrofitModule
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
    private val url = RetrofitModule.SPLASH_BASE_URL + "random"

    suspend fun getRandomSplash(): Drawable? {
        return withContext(dispatcher) {
            val imgRequest = ImageRequest.Builder(context)
                .data(url)
                .size(1080, 1920)
                .crossfade(true)
                .build()
            val result = imageLoader.execute(imgRequest)
            result.drawable
        }
    }
}