package com.genlz.jetpacks.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProcessMonitorService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    @RequiresPermission("android.permission.SET_ACTIVITY_WATCHER")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        CoroutineScope(Dispatchers.Main).launch {
            ProcessMonitorManager.getInstance(applicationContext).topInfoFlow().collectLatest {
                val topTask = it.second
                Log.d(TAG, "onStartCommand: ${topTask.topActivity}")
            }
        }
        return START_STICKY_COMPATIBILITY
    }

    companion object {
        private const val TAG = "ProcessMonitorService"
    }
}



