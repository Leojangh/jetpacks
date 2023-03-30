package com.genlz.jetpacks.threadaffinity

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.Size
import kotlinx.coroutines.CoroutineDispatcher
import java.util.concurrent.*
import java.util.function.Predicate

@Suppress("unused")
object ThreadAffinities {

    /**
     * Instantiate a [AffinityExecutorService] using passed params.
     *
     * @see ThreadPoolExecutor
     */
    @JvmStatic
    fun newAffinityThreadPool(
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
    fun Thread.affiliate(@Size(min = 1) affinity: IntArray): Thread {
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
    fun CoroutineDispatcher.affiliate(@Size(min = 1) affinity: IntArray): CoroutineDispatcher {
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
    fun ExecutorService.affiliate(@Size(min = 1) affinity: IntArray): ExecutorService {
        require(affinity.isNotEmpty())
        return if (this is AffinityExecutorService) setAffinity(affinity)
        else AffinityExecutorServiceDecorator(affinity, this)
    }

    @JvmStatic
    @JvmName("getBaseExecutorService")
    fun ExecutorService.unaffiliate(): ExecutorService =
        when (this) {
            is AffinityExecutorServiceDecorator -> delegate
            is AffinityThreadPoolExecutor ->
                //modify affinity to all
                setAffinity(IntArray(Runtime.getRuntime().availableProcessors(), Int::toInt))

            else -> this
        }

    @JvmStatic
    @JvmName("newAffinityForkJoinPool")
    fun newAffinityForkJoinPool(
        @Size(min = 1)
        affinity: IntArray,
        factory: ForkJoinPool.ForkJoinWorkerThreadFactory = ForkJoinPool.defaultForkJoinWorkerThreadFactory,
        handler: Thread.UncaughtExceptionHandler? = null,
        asyncMode: Boolean = false,
        corePoolSize: Int = 0,
        /**
         * [java.util.concurrent.ForkJoinPool.MAX_CAP]
         */
        maximumPoolSize: Int = 0x7fff,
        minimumRunnable: Int = 1,
        saturate: Predicate<in ForkJoinPool>? = null,
        /**
         * [java.util.concurrent.ForkJoinPool.DEFAULT_KEEPALIVE]
         */
        keepAliveTime: Long = 60000L,
        unit: TimeUnit = TimeUnit.MILLISECONDS,
    ): ForkJoinPool {
        require(affinity.isNotEmpty())
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            AffinityForkJoinPool(
                affinity,
                factory,
                handler,
                asyncMode,
                corePoolSize,
                maximumPoolSize,
                minimumRunnable,
                saturate,
                keepAliveTime,
                unit
            )
        else AffinityForkJoinPool(affinity)
    }

    /*fun ForkJoinPool.unaffilate(): ForkJoinPool {
        return if (this is AffinityForkJoinPool) {
            //Cannot modify parallelism
            affinity = IntStream.range(0, Runtime.getRuntime().availableProcessors()).toArray();this
        } else this
    }*/
}



