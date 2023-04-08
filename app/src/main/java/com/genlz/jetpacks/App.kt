package com.genlz.jetpacks

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.genlz.jetpacks.di.ApplicationScope
import com.genlz.jetpacks.threadaffinity.CpuFrequencyMonitor
import com.genlz.jetpacks.utility.ForegroundTracker
import com.genlz.share.util.appcompat.mainExecutorExt
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import javax.inject.Inject
import kotlin.concurrent.thread
import kotlin.io.path.Path
import kotlin.io.path.readText

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

    @Inject
    lateinit var processName: ProcessName

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        _INSTANCE = this
        super.onCreate()
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