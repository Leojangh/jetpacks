package com.genlz.jetpacks.startup

import android.content.Context
import androidx.startup.Initializer
import com.genlz.jetpacks.service.ProcessMonitorService
import com.genlz.jetpacks.service.RemoteService
import com.genlz.jetpacks.service.WorkerService
import com.genlz.share.util.appcompat.intent

class ServiceZygote : Initializer<Unit> {

    override fun create(context: Context) {
        context.startService(context.intent<RemoteService>())
        context.startService(context.intent<WorkerService>())
        context.startService(context.intent<ProcessMonitorService>())
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }

    companion object {
        private const val TAG = "ServiceZygote"
    }
}