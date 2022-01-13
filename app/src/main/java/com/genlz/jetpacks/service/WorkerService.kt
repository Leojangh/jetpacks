package com.genlz.jetpacks.service

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log

class WorkerService : Service() {

    private val clients = mutableListOf<Messenger>()

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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: ")
        return START_STICKY_COMPATIBILITY
    }

    companion object {
        private const val TAG = "WorkerService"


        const val MSG_HELLO = 1
        const val MSG_REGISTER_CLIENT = 2
        const val MSG_REGISTER_SUCCESS = 3
    }
}