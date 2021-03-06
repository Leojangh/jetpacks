package com.genlz.jetpacks.service

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.genlz.jetpacks.di.ApplicationScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProcessMonitorService : Service() {

    @Inject
    @ApplicationScope
    internal lateinit var scope: CoroutineScope

    override fun onBind(intent: Intent?): IBinder? = null

    @RequiresPermission("android.permission.SET_ACTIVITY_WATCHER")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scope.launch {
            ProcessMonitorManager.getInstance(applicationContext)
                .topInfoFlow()
                .catch {
                    //ignore
                }.collect {
                    val (topProcess, topTask) = it
                    Log.d(TAG, "onStartCommand: ${topTask.topActivity}")
                    Log.d(TAG, "onStartCommand: ${topProcess.processName}")
                }
        }
        return START_STICKY_COMPATIBILITY
    }

    companion object {
        private const val TAG = "ProcessMonitorService"
    }
}



