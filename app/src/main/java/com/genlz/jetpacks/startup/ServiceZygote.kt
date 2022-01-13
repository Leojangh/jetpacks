package com.genlz.jetpacks.startup

import android.app.ActivityThread
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.startup.Initializer
import com.genlz.jetpacks.service.WorkerService

class ServiceZygote : Initializer<Unit> {

    override fun create(context: Context) {
        Log.d(TAG, "create: ${ActivityThread.currentProcessName()}")
        context.startService(Intent(context, WorkerService::class.java))
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }

    companion object {
        private const val TAG = "ServiceZygote"
    }
}