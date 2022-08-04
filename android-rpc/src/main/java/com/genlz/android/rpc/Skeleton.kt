package com.genlz.android.rpc

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.core.os.bundleOf
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

private const val TAG = "Skeleton"

var Message.errorOccurs: Boolean
    get() = arg2 == 1
    set(value) {
        arg2 = if (value) 1 else 0
    }

class Skeleton : Service() {

    private val dispatcher = HandlerThread("Skeleton-dedicated-service")

    private val registry = RemoteProcessRegistry.getInstance()

    private val poolExecutor = ThreadPoolExecutor(
        4,
        4,
        10L,
        TimeUnit.SECONDS,
        LinkedBlockingQueue(),
    )

    private lateinit var messenger: Messenger

    /**
     * 这个回调方法会在[dispatcher]中执行，[dispatcher]仅仅是将调用任务分发至[poolExecutor]中执行
     */
    private val callback = Handler.Callback { msg ->
        val f = registry.functions[msg.what] ?: return@Callback false
        val client = msg.replyTo
        val reply = Message.obtain().apply {
            what = msg.what
        }
        //问题1：msg.replyTo的作用域到这里就停止了？在execute lambda中msg.replyTo为null
        //必须像上面的方式一样用client重新保存一下才能在execute的lambda中使用？msg.what也是如此
        poolExecutor.execute {
            try {
                reply.obj = f(msg.arg1, msg.arg2, msg.obj as? Parcelable)
            } catch (t: Throwable) {
                /**
                 * 在这里传递一些关键的异常信息给客户端，就像[Binder]的异常处理一样
                 */
                reply.errorOccurs = true
                reply.data = bundleOf(
                    "cause" to t.message,
                    "type" to when (t) {
                        is IllegalStateException -> 1
                        is IllegalArgumentException -> 2
                        //etc.
                        else -> 0
                    }
                )
            }
            client.send(reply)
        }
        true
    }

    private lateinit var handler: Handler

    override fun onCreate() {
        super.onCreate()
        dispatcher.start()
        handler = Handler(dispatcher.looper, callback)
        messenger = Messenger(handler)
    }

    override fun onBind(intent: Intent?): IBinder? = messenger.binder

    override fun onDestroy() {
        super.onDestroy()
        dispatcher.quit()
        poolExecutor.shutdown()
        Log.i(TAG, "SkeletonServer destroyed.")
    }
}