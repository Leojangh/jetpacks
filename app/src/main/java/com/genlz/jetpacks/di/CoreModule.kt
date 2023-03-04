package com.genlz.jetpacks.di

import android.app.ActivityThread
import android.app.AlarmManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import com.genlz.jetpacks.App
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.time.Instant
import java.time.ZoneOffset
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * In most cases, they are not necessary.
 */
@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

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