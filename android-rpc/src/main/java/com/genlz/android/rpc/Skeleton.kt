package com.genlz.android.rpc

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import com.genlz.share.sugar4kt.CompletableFutureEx
import com.genlz.share.sugar4kt.CompletableFutureEx.Companion.thenAcceptAsyncKt
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

private const val TAG = "Skeleton"

var Message.errorOccurs: Boolean
    get() = arg2 == 1
    set(value) {
        arg2 = if (value) 1 else 0
    }

@RequiresApi(Build.VERSION_CODES.N)
class Skeleton : Service() {

    private val handlerThread = HandlerThread("Skeleton-dedicated-service")

    private val registry = RemoteProcessRegistry.getInstance()

    private val poolExecutor = ThreadPoolExecutor(
        4,
        4,
        10L,
        TimeUnit.SECONDS,
        LinkedBlockingQueue(),
        object : ThreadFactory {
            private var i = 0
            override fun newThread(r: Runnable?) = Thread(r, "Skeleton-thread-pool-${i++}")
        }
    )

    private lateinit var messenger: Messenger

    /**
     * Workaround:Using Guava ListenableFuture
     */
    private val callback = Handler.Callback { msg ->
        val f = registry.functions[msg.what] ?: return@Callback false
        val client = msg.replyTo
        val what = msg.what
        val reply = Message.obtain()
        CompletableFutureEx.supplyAsync(poolExecutor) {
            f(msg.arg1, msg.arg2, msg.obj as? Parcelable)
        }.exceptionally {
            reply.errorOccurs = true
            reply.data = bundleOf(
                "cause" to it.message
            )
            null
        }.thenAcceptAsyncKt({ handler.post(it) }) {
            reply.what = what
            try {
                reply.obj = it
            } catch (e: Exception) {
                reply.errorOccurs = true
                reply.data = bundleOf(
                    "cause" to e.message
                )
            }
            client.send(reply)
        }
        true
    }

    private lateinit var handler: Handler

    override fun onCreate() {
        super.onCreate()
        handlerThread.start()
        handler = Handler(handlerThread.looper, callback)
        messenger = Messenger(handler)
    }

    override fun onBind(intent: Intent?): IBinder? = messenger.binder

    override fun onDestroy() {
        super.onDestroy()
        handlerThread.quit()
        poolExecutor.shutdown()
        Log.i(TAG, "SkeletonServer destroyed.")
    }
}