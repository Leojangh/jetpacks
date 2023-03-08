package com.genlz.jetpacks

import android.annotation.SuppressLint
import android.app.ActivityThread
import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.genlz.jetpacks.di.ApplicationScope
import com.genlz.jetpacks.libnative.CppNatives
import com.genlz.jetpacks.libnative.RustNatives
import com.genlz.jetpacks.utility.ForegroundTracker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import kotlin.concurrent.thread

/**
 * * System will create different application instance for processes.
 *
 * * For same process,[getSystemService] returns a same instance.
 *
 * * AppStartup dedicate into 'main' process.
 */
@HiltAndroidApp
class App : Application() {

    enum class ProcessName(val processName: String) {
        MAIN(INSTANCE.packageName),
        RPC("${INSTANCE.packageName}:rpc"),
        REMOTE("${INSTANCE.packageName}:remote")
    }

    @Inject
    @ApplicationScope
    internal lateinit var appScope: CoroutineScope

    lateinit var processName: ProcessName

    override fun onCreate() {
        super.onCreate()
        _INSTANCE = this
        processName = ProcessName.values()
            .find { it.processName == ActivityThread.currentProcessName() }
            ?: error("")

        if (processName == ProcessName.MAIN) {
            Toast.makeText(this, RustNatives.hello("Rust"), Toast.LENGTH_SHORT).show()
            RustNatives.runNative()
            thread {
                CppNatives.setAffinity(cpus = intArrayOf(0,1))
                while (true) {
                    Log.d(TAG, "Thread 0 is running on: ${CppNatives.whereAmIRunning()}")
                    Thread.sleep(1000)
                }
            }
            thread {
                CppNatives.setAffinity(cpus = intArrayOf(0,1))
                while (true) {
                    Log.d(TAG, "Thread 1 is running on: ${CppNatives.whereAmIRunning()}")
                    Thread.sleep(1000)
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