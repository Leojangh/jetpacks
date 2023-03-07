package com.genlz.jetpacks.service

import android.app.ActivityManager
import android.app.IProcessObserver
import android.app.TaskInfo
import android.content.ComponentName
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import org.lsposed.hiddenapibypass.HiddenApiBypass

@RequiresApi(Build.VERSION_CODES.Q)
class ProcessMonitorManager private constructor() {

    private val clazzIActivityManager = Class.forName("android.app.IActivityManager")

    private val iActivityManager =
        HiddenApiBypass.invoke(ActivityManager::class.java, null, "getService")

    @RequiresPermission("android.permission.SET_ACTIVITY_WATCHER")
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

            private fun refreshTopInfo() {
                val rootTaskInfos = HiddenApiBypass.invoke(
                    clazzIActivityManager,
                    iActivityManager,
                    "getAllRootTaskInfos"/*Android S*/
                ) as List<*>
                val topActivity =
                    TaskInfo::class.java.getField("topActivity")[rootTaskInfos[0]] as ComponentName
                trySend(topActivity.flattenToString())
            }
        }
        HiddenApiBypass.invoke(
            clazzIActivityManager,
            iActivityManager,
            "registerProcessObserver",
            cb
        )
        awaitClose {
            HiddenApiBypass.invoke(
                clazzIActivityManager,
                iActivityManager,
                "unregisterProcessObserver",
                cb
            )
        }
    }

    companion object {

        @Volatile
        @JvmStatic
        private var INSTANCE: ProcessMonitorManager? = null

        @JvmStatic
        fun getInstance(): ProcessMonitorManager {
            if (INSTANCE == null) {
                synchronized(ProcessMonitorManager::class.java) {
                    if (INSTANCE == null)
                        INSTANCE = ProcessMonitorManager()
                }
            }
            return INSTANCE!!
        }
    }
}
