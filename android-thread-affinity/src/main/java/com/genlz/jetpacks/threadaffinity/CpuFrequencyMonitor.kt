package com.genlz.jetpacks.threadaffinity

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import androidx.annotation.RequiresApi
import com.topjohnwu.superuser.ipc.RootService
import com.topjohnwu.superuser.nio.FileSystemManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchKey
import java.util.concurrent.CountDownLatch
import kotlin.io.path.Path
import kotlin.io.path.readText

//TODO not working.
@RequiresApi(Build.VERSION_CODES.Q)
class CpuFrequencyMonitor private constructor(private val ctx: Context) {

    private var fs: FileSystemManager? = null
    private val latch = CountDownLatch(1)

    fun curFreqOf(id: Int): Flow<Int> = flow {
        latch.await()
        
        val file =
            fs?.getFile("/sys/devices/system/cpu/cpu$id/cpufreq/cpuinfo_cur_freq") ?: error("")
        emit(file.readText().trim().toInt())
    }.flowOn(Dispatchers.IO)

    init {
        connect()
    }

    class CpuFrequencyMonitorService : RootService() {

        override fun onBind(intent: Intent): IBinder {
            Log.d(TAG, "onBind: ...")
            return FileSystemManager.getService()
        }

        private fun monitor(id: Int) = flow {
            val watcher = FileSystems.getDefault().newWatchService()
            Path("/sys/devices/system/cpu/cpu$id/cpufreq").register(
                watcher,
                StandardWatchEventKinds.ENTRY_MODIFY,
            )
            var key: WatchKey
            while (watcher.take().also { key = it } != null) {
                for (event in key.pollEvents()) {
                    val target = event.context() as Path
                    Log.d(TAG, "curFreqOf: $target")
                    if (target.endsWith("cpuinfo_cur_freq")) {
                        emit(target.readText().trim().toInt())
                    }
                }
                key.reset()
            }
        }.flowOn(Dispatchers.IO)

    }

    private fun connect() {
        RootService.bind(Intent(
            ctx,
            CpuFrequencyMonitorService::class.java
        ), object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                try {
                    fs = FileSystemManager.getRemote(service)
                    latch.countDown()
                } catch (_: RemoteException) {
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                fs = null
            }
        })
    }

    companion object {

        private const val TAG = "MainActivity"

        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: CpuFrequencyMonitor? = null
        fun getInstance(ctx: Context): CpuFrequencyMonitor {
            if (null == INSTANCE) {
                synchronized(CpuFrequencyMonitor::class.java) {
                    if (null == INSTANCE) INSTANCE = CpuFrequencyMonitor(ctx.applicationContext)
                }
            }
            return INSTANCE!!
        }
    }
}