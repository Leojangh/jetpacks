package com.genlz.jetpacks

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import com.genlz.jetpacks.utility.ForegroundTracker
import com.genlz.libnative.functionInNative
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        _INSTANCE = this

    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        registerActivityLifecycleCallbacks(ForegroundTracker)
    }

    companion object {

        const val TAG = "App"

        private external fun androidJni(): Int

        init {
            System.loadLibrary("jetpacks")
            Log.d(TAG, ": ${androidJni()}")
            functionInNative()
        }

        /**
         * Get application context everywhere.
         *
         * Note:This is available after the [Application.onCreate] being called.
         *
         * It will always be recommended to pass in context as a parameter instead of hard-code with this value
         * in third-part component,so that dependency injection can show it's power.Rely on this value as a last resort.
         */
        @JvmStatic
        val INSTANCE
            get() = _INSTANCE ?: error("This value is unavailable before Application.onCreate!")

        @Suppress("ObjectPropertyName")
        @SuppressLint("StaticFieldLeak")
        private var _INSTANCE: Context? = null
    }
}