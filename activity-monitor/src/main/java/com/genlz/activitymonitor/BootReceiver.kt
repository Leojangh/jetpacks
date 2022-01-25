package com.genlz.activitymonitor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.genlz.share.util.appcompat.intent
import com.genlz.share.util.appcompat.startForegroundServiceExt

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive: ${intent.action}")
        context.startForegroundServiceExt(context.intent<ProcessMonitorService>())
    }

    companion object {
        private const val TAG = "BootReceiver"
    }
}