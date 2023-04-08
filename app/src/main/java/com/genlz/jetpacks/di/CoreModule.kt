package com.genlz.jetpacks.di

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.genlz.jetpacks.App
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

/**
 * In most cases, they are not necessary.
 */
@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Provides
    @RequiresApi(Build.VERSION_CODES.P)
    fun provideProcessName(): App.ProcessName {
        return App.ProcessName.values().first { it.processName == Application.getProcessName() }
    }

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()
    }
}