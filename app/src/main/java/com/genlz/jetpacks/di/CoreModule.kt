package com.genlz.jetpacks.di

import android.app.AlarmManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
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
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager {
        return ContextCompat.getSystemService(
            context,
            ConnectivityManager::class.java
        ) as ConnectivityManager
    }

    @Provides
    fun provideAlarmManager(@ApplicationContext context: Context): AlarmManager {
        return ContextCompat.getSystemService(context, AlarmManager::class.java) as AlarmManager
    }

    @Provides
    fun provideMainHandler(): Handler {
        return Handler(Looper.getMainLooper())
    }

    @Provides
    fun provideScheduledExecutor(): ScheduledExecutorService {
        return Executors.newScheduledThreadPool(1)
    }

    @Provides
    fun provideZoneOffset(): ZoneOffset {
        return ZoneOffset.systemDefault().rules.getOffset(Instant.now())
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