package com.genlz.android.rpc

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.core.os.bundleOf
import com.genlz.share.util.appcompat.getSerializableExt
import java.io.Serializable
import java.util.UUID
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

private const val TAG = "Skeleton"

internal var Message.errorOccurs: Boolean
    get() = arg2 == 1
    set(value) {
        arg2 = if (value) 1 else 0
    }

private const val KEY_FOR_SERIALIZABLE_TYPE = "Skeleton.ResultType.Serializable"
internal const val KEY_FOR_EXCEPTION = "Skeleton.Exception"
internal const val KEY_FOR_CALLER_ID = "Skeleton.CallerId"
internal const val KEY_FOR_ROW_DATA = "Skeleton.RowData"

/**
 * 将[Serializable]包装成[Parcelable]类型以进行跨进程传递
 */
internal fun Serializable.wrap() = bundleOf(KEY_FOR_SERIALIZABLE_TYPE to this)

/**
 * 从[Bundle]包装器中解析出原本的[Serializable]类型
 */
@Suppress("DEPRECATION")
internal fun Bundle.unwrap() = getSerializable(KEY_FOR_SERIALIZABLE_TYPE)

internal fun getCallerId(bundle: Bundle): UUID =
    bundle.getSerializableExt(KEY_FOR_CALLER_ID) ?: error("Expected caller id is null!")

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
        val f = registry.functions[msg.what]
            ?: error("Target method with id:${msg.what} is not found!")
        val client = msg.replyTo
        val reply = Message.obtain().apply {
            what = msg.what
        }
        val (rowData, caller) = with(msg.data) {
            getBundle(KEY_FOR_ROW_DATA) to getCallerId(this)
        }
        Log.d(TAG, "handle request for: $caller")
        reply.data = bundleOf(KEY_FOR_CALLER_ID to caller)
        //问题1：msg.replyTo的作用域到这里就停止了？在execute lambda中msg.replyTo为null
        //必须像上面的方式一样用client重新保存一下才能在execute的lambda中使用？msg.what也是如此
        poolExecutor.execute {
            try {
                reply.obj = f(msg.arg1, msg.arg2, msg.obj as? Parcelable, rowData).run {
                    //如果this不是Parcelable类型的话异常交给Framework来处理：can't marshal non-Parcelable type.
                    (this as? Serializable)?.wrap() ?: this
                }
            } catch (e: Exception) {
                reply.errorOccurs = true
                reply.data.putSerializable(KEY_FOR_EXCEPTION, e)
            }
            client.send(reply)
        }
        true
    }

    override fun onCreate() {
        super.onCreate()
        dispatcher.start()
        messenger = Messenger(Handler(dispatcher.looper, callback))
    }

    override fun onBind(intent: Intent?): IBinder? = messenger.binder

    override fun onDestroy() {
        super.onDestroy()
        dispatcher.quit()
        poolExecutor.shutdown()
        Log.i(TAG, "SkeletonServer destroyed.")
    }
}