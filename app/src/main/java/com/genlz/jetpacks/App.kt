package com.genlz.jetpacks

import android.annotation.SuppressLint
import android.app.ActivityThread
import android.app.Application
import android.content.Context
import android.util.Log
import com.genlz.android.rpc.ServerStub
import com.genlz.jetpacks.di.ApplicationScope
import com.genlz.jetpacks.utility.ForegroundTracker
import com.genlz.libnative.functionInNative
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.Executors
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

        if (ActivityThread.currentProcessName() == Process.MAIN.processName) {
            val stub = ServerStub(this)
            val pool = Executors.newFixedThreadPool(10)
            repeat(10) {
                pool.execute {
                    Log.d(TAG, "onCreate: $it")
                    stub.callAsync(1) { r: List<String> ->
                        Log.d(TAG, "onCreate $it: $r")
                    }
                }
            }
        }

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