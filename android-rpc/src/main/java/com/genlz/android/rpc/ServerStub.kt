package com.genlz.android.rpc

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import androidx.collection.ArrayMap
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.LinkedBlockingQueue

class ServerStub(private val ctx: Context) {

    private lateinit var remoteService: Messenger

    private lateinit var thisProxy: Messenger

    private val callbacks = ArrayMap<Int, Callback<Parcelable>>()

    private val messages = LinkedBlockingQueue<Message>()

    private val handler = Handler(
        Looper.myLooper() ?: throw IllegalStateException("Current thread has no associated looper!")
    ) { msg ->
        if (messages.isEmpty()) {
            messages += Message.obtain().apply { what = STOP_SEND }
        }
        callbacks[msg.what]?.let {
            if (msg.errorOccurs) {
                it.onException(readException(msg))
            } else {
                it.onSuccess(msg.obj as? Parcelable)
            }
            true
        } ?: false
    }

    private fun readException(msg: Message): Exception {
        val cause = msg.data["cause"] as String
        return when (msg.data.getInt("type", 0)) {
            1 -> IllegalStateException(cause)
            2 -> IllegalArgumentException(cause)
            else/*0*/ -> RemoteException(cause)
        }
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

    /**
     * 异步调用远程服务器中的方法，结果通过[callback]回调传回，它在发起者的线程中执行，异常也是。
     */
    @JvmOverloads
    @Suppress("UNCHECKED_CAST")
    fun callAsync(
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

    /**
     * 由协程驱动的[callAsync]方法
     */
    @OptIn(InternalCoroutinesApi::class)
    suspend fun <R : Parcelable> call(
        method: Int,
        arg1: Int = 0,
        arg2: Int = 0,
        obj: Parcelable? = null,
        data: Bundle? = null,
    ) = suspendCancellableCoroutine {
        callAsync(method, arg1, arg2, obj, data, object : Callback<R> {

            override fun onSuccess(result: R?) {
                it.tryResume(result)?.let { token ->
                    it.completeResume(token)
                }
            }

            override fun onException(e: Exception) {
                it.tryResumeWithException(e)?.let { token ->
                    it.completeResume(token)
                }
            }
        })
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