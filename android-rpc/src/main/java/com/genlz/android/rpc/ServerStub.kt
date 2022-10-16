package com.genlz.android.rpc

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import androidx.core.os.bundleOf
import com.genlz.share.util.appcompat.getSerializableExt
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.*
import java.util.concurrent.*

class ServerStub(private val ctx: Context) {

    private lateinit var remoteService: Messenger

    private lateinit var thisProxy: Messenger

    private val callbacks: ConcurrentMap<UUID, Pair<Executor, Callback<*>>> = ConcurrentHashMap()

    private val messages: BlockingQueue<Message> = LinkedBlockingQueue()

    private val handlerThread = HandlerThread(ServerStub::class.java.simpleName + "-Thread")

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
        handlerThread.start()
    }

    private val handler = Handler(handlerThread.looper) { msg ->
        if (messages.isEmpty()) {
            Message.obtain().apply { what = STOP_SEND }.let(messages::put)
        }
        val caller = getCallerId(msg.data)
        Log.d(TAG, "received callback:  $caller")
        callbacks[caller]?.let { (executor, cb) ->
            if (msg.errorOccurs) {
                executor.execute { cb.onException(readException(msg)) }
            } else {
                val result = with(msg.obj) {
                    //如果结果是Bundle包装的Serializable，则返回这个Serializable，否则返回原本类型
                    (this as? Bundle)?.unwrap() ?: this
                }
                executor.execute {
                    @Suppress("UNCHECKED_CAST")
                    (cb as Callback<Any>).onSuccess(result)
                }
            }
            callbacks.remove(caller)
        }
        true
    }

    private fun readException(msg: Message): Exception =
        msg.data.getSerializableExt(KEY_FOR_EXCEPTION) ?: error("Expected exception not found!")

    private val senderThread: Thread = Thread {
        while (true) {
            val m = messages.take()
            if (m.what == STOP_SEND) break

            m.replyTo = thisProxy
            remoteService.send(m)
        }
        ctx.unbindService(conn)
        handlerThread.quitSafely()
    }

    /**
     * 异步调用远程服务器中的方法，结果通过[callback]回调传回。
     * 请合理处理异常，默认情况下异常会被直接抛出。
     *
     * @param R 远程方法的返回值类型
     * @param method 远程方法的唯一ID
     * @param arg1 可传递给远程方法的第一个[Int]参数
     * @param arg2 可传递给远程方法的第二个[Int]参数
     * @param obj 可传递给远程方法的第三个[Parcelable]参数
     * @param data 可传递给远程方法的第四个[Bundle]参数
     * @param executor 决定[callback]在哪里完成
     * @param callback 远程方法运行完成接受结果的回调
     */
    @JvmOverloads
    @Suppress("UNCHECKED_CAST")
    fun <R> callAsync(
        method: Int,
        arg1: Int = 0,
        arg2: Int = 0,
        obj: Parcelable? = null,
        data: Bundle? = null,
        executor: Executor = Executor { handler.post(it) },
        callback: Callback<R>? = null,
    ) = callAsyncInternal(
        generateCallerId(),
        method,
        arg1,
        arg2,
        obj,
        data,
        executor,
        callback
    )

    private fun callAsyncInternal(
        caller: UUID,
        method: Int,
        arg1: Int,
        arg2: Int,
        obj: Parcelable?,
        data: Bundle?,
        executor: Executor,
        callback: Callback<*>?,
    ) {
        callbacks[caller] = executor to callback as Callback<*>
        Message.obtain().apply {
            this.what = method
            this.obj = obj
            this.arg1 = arg1
            this.arg2 = arg2
            this.data = bundleOf(KEY_FOR_ROW_DATA to data, KEY_FOR_CALLER_ID to caller)
        }.let(messages::put)
    }

    private fun generateCallerId() = UUID.randomUUID()

    /**
     * 由协程驱动的[callAsync]方法
     *
     * @see callAsync
     */
    @OptIn(InternalCoroutinesApi::class)
    suspend fun <R> call(
        method: Int,
        arg1: Int = 0,
        arg2: Int = 0,
        obj: Parcelable? = null,
        data: Bundle? = null,
    ): R = suspendCancellableCoroutine {
        val caller = generateCallerId()
        val callback = object : Callback<R> {

            override fun onSuccess(result: R) {
                it.tryResume(result)?.let(it::completeResume)
            }

            override fun onException(e: Exception) {
                it.tryResumeWithException(e)?.let(it::completeResume)
            }
        }
        callAsyncInternal(caller, method, arg1, arg2, obj, data, handler::post, callback)
        it.invokeOnCancellation {
            callbacks.remove(caller)
        }
    }

    companion object {

        private const val TAG = "ServerStub"

        private const val STOP_SEND = Int.MIN_VALUE

        @JvmStatic
        fun getTargetService(ctx: Context) = Intent(ctx, Skeleton::class.java)
    }
}