package com.genlz.jetpacks.threadaffinity

import android.annotation.SuppressLint
import androidx.annotation.Size
import kotlinx.coroutines.CoroutineDispatcher
import java.util.concurrent.*

@Suppress("unused")
object ThreadAffinities {

    /**
     * Instantiate a [AffinityExecutorService] using passed params.
     *
     * @see ThreadPoolExecutor
     */
    @JvmStatic
    fun newAffinityExecutorService(
        @Size(min = 1)
        affinity: IntArray,
        corePoolSize: Int = Runtime.getRuntime().availableProcessors(),
        maximumPoolSize: Int = Runtime.getRuntime().availableProcessors(),
        keepAliveTime: Long = 1L,
        unit: TimeUnit = TimeUnit.SECONDS,
        workQueue: BlockingQueue<Runnable> = LinkedBlockingQueue(),
        threadFactory: ThreadFactory = Executors.defaultThreadFactory(),
        handler: RejectedExecutionHandler = ThreadPoolExecutor.AbortPolicy(),
    ): ExecutorService = AffinityThreadPoolExecutor(
        if (affinity.isNotEmpty()) affinity else throw IllegalArgumentException("affinity is empty."),
        corePoolSize,
        maximumPoolSize,
        keepAliveTime,
        unit,
        workQueue,
        threadFactory,
        handler
    )

    /**
     * Unstable
     */
    @JvmStatic
    @JvmName("setThreadAffinity")
    fun Thread.affinity(@Size(min = 1) affinity: IntArray): Thread {
        require(affinity.isNotEmpty())
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
    /**
     * Unstable
     */
    @JvmStatic
    @JvmName("newAffinityDispatcher")
    fun CoroutineDispatcher.affinity(@Size(min = 1) affinity: IntArray): CoroutineDispatcher {
        require(affinity.isNotEmpty())
        return if (this is ThreadAffinity) {
            this.affinity = affinity;this
        } else AffinityCoroutineDispatcherDecorator(affinity, this)
    }

    /**
     * Create an [AffinityExecutorService] from an [ExecutorService],or modify affinity if it is already an [AffinityExecutorService].
     */
    @JvmStatic
    @JvmName("newAffinityExecutorService")
    fun ExecutorService.affinity(@Size(min = 1) affinity: IntArray): ExecutorService {
        require(affinity.isNotEmpty())
        return if (this is AffinityExecutorService) {
            this.affinity = affinity;this
        } else AffinityExecutorServiceDecorator(affinity, this)
    }
}



