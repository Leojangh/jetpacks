package com.genlz.activitymonitor

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.view.WindowManager
import androidx.annotation.RequiresPermission
import com.genlz.activitymonitor.databinding.FloatingWindowBinding
import com.genlz.share.util.appcompat.getSystemService
import com.genlz.share.util.appcompat.lazyNoneSafe
import kotlinx.coroutines.*

class ProcessMonitorService : Service() {

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val binding by lazyNoneSafe { FloatingWindowBinding.inflate(getSystemService()) }

    private val windowManager: WindowManager by lazyNoneSafe { getSystemService() }

    override fun onBind(intent: Intent?): IBinder? = null

    @RequiresPermission("android.permission.SET_ACTIVITY_WATCHER")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        @Suppress("DEPRECATION")
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            0,
            0,
            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
            0,
            0
        )
        if (!binding.root.isAttachedToWindow) {
            windowManager.addView(binding.root, params)
        }
        scope.launch {
            ProcessMonitorManager.getInstance(applicationContext)
                .topInfoFlow()
                .collect {
                    val (_, topTask) = it
                    Log.d(TAG, "onStartCommand: ${topTask.topActivity}")
                    withContext(Dispatchers.Main) {
                        binding.activity.text =
                            topTask.topActivity?.packageName + "/" + topTask.topActivity?.className
                    }
                }
        }
        return START_STICKY_COMPATIBILITY
    }

    companion object {
        private const val TAG = "ProcessMonitorService"
    }
}



