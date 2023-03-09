package com.genlz.jetpacks.threadaffinity

import android.annotation.SuppressLint
import kotlinx.coroutines.CoroutineDispatcher
import java.util.concurrent.*

object ThreadAffinities {

    /**
     * Instantiate a [AffinityExecutorService] using passed params.
     *
     * @see ThreadPoolExecutor
     */
    @JvmStatic
    fun newAffinityExecutorService(
        affinity: IntArray = intArrayOf(),
        corePoolSize: Int = Runtime.getRuntime().availableProcessors(),
        maximumPoolSize: Int = Runtime.getRuntime().availableProcessors(),
        keepAliveTime: Long = 1L,
        unit: TimeUnit = TimeUnit.SECONDS,
        workQueue: BlockingQueue<Runnable> = LinkedBlockingQueue(),
        threadFactory: ThreadFactory = Executors.defaultThreadFactory(),
        handler: RejectedExecutionHandler = ThreadPoolExecutor.AbortPolicy(),
    ): ExecutorService = AffinityThreadPoolExecutor(
        affinity,
        corePoolSize,
        maximumPoolSize,
        keepAliveTime,
        unit,
        workQueue,
        threadFactory,
        handler
    )

    @JvmStatic
    @JvmName("setThreadAffinity")
    fun Thread.affinity(affinity: IntArray = intArrayOf()): Thread {
        if (affinity.isEmpty()) return this
        if (state != Thread.State.NEW) error("Only state in NEW is supported.")
        @SuppressLint("DiscouragedPrivateApi")
        val field = Thread::class.java.getDeclaredField("target")
        field.isAccessible = true
        val target = field[this] as? Runnable
            ?: error("You should construct a Thread by passing a Runnable.")
        field[this] =
            if (target is ThreadAffinity) target else AffinityRunnableWrapper(affinity, target)
        return this
    }

    //bug:Invalid argument
    @JvmStatic
    @JvmName("newAffinityDispatcher")
    fun CoroutineDispatcher.affinity(affinity: IntArray = intArrayOf()) =
        if (this is ThreadAffinity || affinity.isEmpty()) this
        else AffinityCoroutineDispatcherDecorator(affinity, this)

    /**
     * Create an [AffinityExecutorService] from an [ExecutorService].
     */
    @JvmStatic
    @JvmName("newAffinityExecutorService")
    fun ExecutorService.affinity(affinity: IntArray = intArrayOf()) =
        if (this is AffinityExecutorService || affinity.isEmpty()) this
        else AffinityExecutorServiceDecorator(affinity, this)
}



