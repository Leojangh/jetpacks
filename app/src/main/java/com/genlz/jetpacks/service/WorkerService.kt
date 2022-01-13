package com.genlz.jetpacks.service

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import com.genlz.jetpacks.di.ApplicationScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WorkerService : Service() {

    private val clients = mutableListOf<Messenger>()

    @Inject
    @ApplicationScope
    internal lateinit var scope: CoroutineScope

    override fun onBind(intent: Intent?): IBinder =
        Messenger(object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MSG_HELLO -> Log.d(TAG, "handleMessage: ${Thread.currentThread()}")

                    MSG_REGISTER_CLIENT -> {
                        val client = msg.replyTo
                        clients += client
                        client.send(Message.obtain(null, MSG_REGISTER_SUCCESS))
                    }
                }
            }
        }).binder

    private fun notifyAllClients(msg: Message) {
        clients.forEach {
            it.send(msg)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND)
        scope.launch {
            while (true) {
                delay(1000L)
            }
        }
        return START_STICKY_COMPATIBILITY
    }

    companion object {
        private const val TAG = "WorkerService"

        const val MSG_HELLO = 1
        const val MSG_REGISTER_CLIENT = 2
        const val MSG_REGISTER_SUCCESS = 3
    }
}