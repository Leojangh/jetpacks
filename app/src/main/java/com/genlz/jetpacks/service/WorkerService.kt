package com.genlz.jetpacks.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.*
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.core.os.LocaleListCompat
import com.genlz.jetpacks.di.ApplicationScope
import com.genlz.share.util.appcompat.getSystemService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class WorkerService : Service() {

    private val clients = mutableListOf<Messenger>()

    private val receivers = mutableListOf(
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Toast.makeText(applicationContext, "locale changed", Toast.LENGTH_SHORT).show()
            }
        } to Intent.ACTION_LOCALE_CHANGED,
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Toast.makeText(
                    applicationContext,
                    "config changed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } to Intent.ACTION_CONFIGURATION_CHANGED
    )

    @Inject
    @ApplicationScope
    internal lateinit var scope: CoroutineScope

    //Only create once.
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
        receivers.forEach {
            registerReceiver(it.first, IntentFilter(it.second))
        }
        return START_STICKY_COMPATIBILITY
    }

    override fun onDestroy() {
        super.onDestroy()
        receivers.forEach {
            unregisterReceiver(it.first)
        }
    }

    companion object {
        private const val TAG = "WorkerService"

        const val MSG_HELLO = 1
        const val MSG_REGISTER_CLIENT = 2
        const val MSG_REGISTER_SUCCESS = 3
    }
}