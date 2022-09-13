package com.genlz.android.rpc

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import androidx.collection.ArrayMap
import java.util.concurrent.LinkedBlockingQueue

class ServerStub(private val ctx: Context) {

    private lateinit var remoteService: Messenger

    private lateinit var thisProxy: Messenger

    private val callbacks = ArrayMap<Int, Callback<Parcelable>>()

    private val messages = LinkedBlockingQueue<Message>()

    private val handler = Handler(
        Looper.myLooper()
            ?: throw IllegalStateException("Current thread has no associated looper!")
    ) { msg ->
        if (messages.isEmpty()) {
            messages += Message.obtain().apply { what = STOP_SEND }
        }
        callbacks[msg.what]?.let {
            if (msg.errorOccurs) {
                val cause = msg.data.getString("cause")
                it.onException(RemoteException(cause))
                Log.d(TAG, "error 1: $cause")
            } else {
                it.onSuccess(msg.obj as? Parcelable)
            }
            true
        } ?: false
    }

    private val senderThread: Thread = Thread {
        while (true) {
            val m = messages.take()
            if (m.what == STOP_SEND) break

            m.replyTo = thisProxy
            remoteService.send(m)
        }
        ctx.unbindService(conn)
    }

    private val conn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            remoteService = Messenger(service)
            thisProxy = Messenger(handler)
            senderThread.start()
        }

        override fun onServiceDisconnected(name: ComponentName?) {}
    }

    init {
        ctx.bindService(getTargetService(ctx), conn, Context.BIND_AUTO_CREATE)
    }

    @JvmOverloads
    @Suppress("UNCHECKED_CAST")
    fun call(
        method: Int,
        arg1: Int = 0,
        arg2: Int = 0,
        obj: Parcelable? = null,
        data: Bundle? = null,
        callback: Callback<*>? = null,
    ) {
        callbacks[method] = callback as? Callback<Parcelable>
        messages += Message.obtain().apply {
            this.what = method
            this.obj = obj
            this.arg1 = arg1
            this.arg2 = arg2
            this.data = data
        }
    }

    companion object {

        private const val TAG = "ServerStub"

        private const val STOP_SEND = Int.MIN_VALUE

        fun getTargetService(ctx: Context): Intent =
            Intent().setClassName(ctx.packageName, "com.genlz.android.rpc.Skeleton")

        @JvmStatic
        fun isRemoteServiceExisted(ctx: Context): Boolean {
            return ctx.packageManager.resolveService(getTargetService(ctx), 0) != null
        }
    }

    fun interface Callback<in R : Parcelable> {
        fun onSuccess(result: R?)

        fun onException(e: Exception) {
            throw RuntimeException(e)
        }
    }
}