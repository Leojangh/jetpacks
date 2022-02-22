package com.genlz.jetpacks.service

import android.app.ActivityManager
import android.app.IProcessObserver
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.genlz.share.util.appcompat.getSystemService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import org.lsposed.hiddenapibypass.HiddenApiBypass

class ProcessMonitorManager private constructor(
    context: Context,
) {

    private val activityManager = context.getSystemService<ActivityManager>()

    @RequiresPermission("android.permission.SET_ACTIVITY_WATCHER")
    @RequiresApi(Build.VERSION_CODES.P)
    fun topInfoFlow() = callbackFlow {
        val cb = object : IProcessObserver.Stub() {
            override fun onForegroundActivitiesChanged(
                pid: Int,
                uid: Int,
                foregroundActivities: Boolean,
            ) {
                refreshTopInfo()
            }

            override fun onForegroundServicesChanged(pid: Int, uid: Int, serviceTypes: Int) {}

            override fun onProcessDied(pid: Int, uid: Int) {
                refreshTopInfo()
            }

            @Suppress("DEPRECATION")
            private fun refreshTopInfo() {
                val topTask =
                    activityManager.getRunningTasks(1)?.get(0) ?: error("No task running!")
                val topProcess =
                    activityManager.runningAppProcesses?.get(0) ?: error("No process running!")
                trySend(topProcess to topTask)
            }
        }
        val iActivityManager =
            HiddenApiBypass.invoke(ActivityManager::class.java, null, "getService")
        val clazz = Class.forName("android.app.IActivityManager")
        HiddenApiBypass.invoke(clazz, iActivityManager, "registerProcessObserver", cb)
        awaitClose {
            HiddenApiBypass.invoke(clazz, iActivityManager, "unregisterProcessObserver", cb)
        }
    }

    companion object {

        @Volatile
        @JvmStatic
        private var INSTANCE: ProcessMonitorManager? = null

        @JvmStatic
        fun getInstance(context: Context): ProcessMonitorManager {
            if (INSTANCE == null) {
                synchronized(ProcessMonitorManager::class.java) {
                    if (INSTANCE == null)
                        INSTANCE = ProcessMonitorManager(context.applicationContext)
                }
            }
            return INSTANCE!!
        }
    }
}
