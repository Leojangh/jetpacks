package com.genlz.jetpacks

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.widget.Toast
import com.genlz.jetpacks.libnative.RustNatives
import com.genlz.jetpacks.di.ApplicationScope
import com.genlz.jetpacks.utility.ForegroundTracker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

/**
 * * System will create different application instance for processes.
 *
 * * For same process,[getSystemService] returns a same instance.
 *
 * * AppStartup dedicate into 'main' process.
 */
@HiltAndroidApp
class App : Application() {

    enum class Process(val processName: String) {
        MAIN(INSTANCE.packageName),
        RPC("${INSTANCE.packageName}:rpc"),
        REMOTE("${INSTANCE.packageName}:remote")
    }

    @Inject
    @ApplicationScope
    internal lateinit var appScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        _INSTANCE = this
        Toast.makeText(this, RustNatives.hello("Rust"), Toast.LENGTH_SHORT).show()
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