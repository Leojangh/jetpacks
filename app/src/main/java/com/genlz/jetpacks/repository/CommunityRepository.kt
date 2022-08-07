package com.genlz.jetpacks.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class CommunityRepository @Inject constructor(
    private val fcmDatasource: FcmDatasource,
) {
    fun getFcm() = fcmDatasource.getFcm()
}

private const val TAG = "CommunityRepository"

class FcmDatasource @Inject constructor(
    @ApplicationContext
    private val context: Context,
) {
    private val intentFilter = IntentFilter("fcm.test")

    fun getFcm() = callbackFlow {
        val receiver = Receiver(this)
        context.registerReceiver(receiver, intentFilter)
        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }

    /**
     * 模拟接受fcm消息
     */
    private class Receiver(
        private val scope: ProducerScope<String>,
    ) : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            val data = intent?.extras?.getString("data") ?: return
            Log.d(TAG, "onReceive: $data")
            scope.trySend(data)
        }
    }
}