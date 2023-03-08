package com.genlz.jetpacks.threadaffinity

import com.genlz.jetpacks.libnative.CppNatives
import java.util.concurrent.*

/**
 * A kind of [ExecutorService],the internal thread has thread affinity.
 */
interface AffinityExecutorService : ExecutorService, ThreadAffinity {

    companion object {

        /**
         * Instantiate a [AffinityExecutorService] using passed params.
         *
         * @see ThreadPoolExecutor
         */
        @JvmStatic
        fun newAffinityExecutor(
            affinity: IntArray = intArrayOf(),
            corePoolSize: Int = Runtime.getRuntime().availableProcessors(),
            maximumPoolSize: Int = Runtime.getRuntime().availableProcessors(),
            keepAliveTime: Long = 1L,
            unit: TimeUnit = TimeUnit.SECONDS,
            workQueue: BlockingQueue<Runnable> = LinkedBlockingQueue(),
            threadFactory: ThreadFactory = Executors.defaultThreadFactory(),
            handler: RejectedExecutionHandler = ThreadPoolExecutor.AbortPolicy(),
        ): AffinityExecutorService = AffinityThreadPoolExecutor(
            affinity,
            corePoolSize,
            maximumPoolSize,
            keepAliveTime,
            unit,
            workQueue,
            threadFactory,
            handler
        )

        /**
         * Create an [AffinityExecutorService] from an [ExecutorService].
         */
        @JvmStatic
        fun newAffinityExecutor(
            affinity: IntArray = intArrayOf(),
            delegate: ExecutorService,
        ): AffinityExecutorService =
            if (delegate is AffinityExecutorService) delegate
            else AffinityDecorator(affinity, delegate)
    }
}

interface ThreadAffinity {
    val affinity: IntArray
}

internal class AffinityRunnableWrapper(
    override val affinity: IntArray,
    private val command: Runnable,
) : Runnable, ThreadAffinity {
    override fun run() {
        if (affinity.isNotEmpty()) {
            CppNatives.setAffinity(cpus = affinity)
        }
        command.run()
    }
}

internal class AffinityCallableWrapper<V>(
    override val affinity: IntArray,
    private val task: Callable<V>,
) : Callable<V>, ThreadAffinity {
    override fun call(): V {
        if (affinity.isNotEmpty()) {
            CppNatives.setAffinity(cpus = affinity)
        }
        return task.call()
    }
}

internal class AffinityThreadPoolExecutor(
    override val affinity: IntArray,
    corePoolSize: Int,
    maximumPoolSize: Int,
    keepAliveTime: Long,
    unit: TimeUnit,
    workQueue: BlockingQueue<Runnable>,
    threadFactory: ThreadFactory,
    handler: RejectedExecutionHandler,
) : ThreadPoolExecutor(
    corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, {
        threadFactory.newThread(AffinityRunnableWrapper(affinity, it))
    }, handler
), AffinityExecutorService

internal class AffinityDecorator(
    override val affinity: IntArray,
    private val delegate: ExecutorService,
) : ExecutorService by delegate, AffinityExecutorService {
    override fun execute(command: Runnable) =
        delegate.execute(AffinityRunnableWrapper(affinity, command))

    override fun <T : Any> submit(task: Callable<T>): Future<T> =
        delegate.submit(AffinityCallableWrapper(affinity, task))

    override fun <T : Any> submit(task: Runnable, result: T): Future<T> =
        delegate.submit(AffinityRunnableWrapper(affinity, task), result)

    override fun submit(task: Runnable): Future<*> =
        delegate.submit(AffinityRunnableWrapper(affinity, task))

    override fun <T : Any> invokeAll(
        tasks: MutableCollection<out Callable<T>>,
    ): MutableList<Future<T>> = delegate.invokeAll(tasks.map {
        AffinityCallableWrapper(affinity, it)
    })

    override fun <T : Any> invokeAll(
        tasks: MutableCollection<out Callable<T>>,
        timeout: Long,
        unit: TimeUnit,
    ): MutableList<Future<T>> = delegate.invokeAll(tasks.map {
        AffinityCallableWrapper(affinity, it)
    }, timeout, unit)

    override fun <T : Any> invokeAny(
        tasks: MutableCollection<out Callable<T>>,
    ): T = delegate.invokeAny(tasks.map {
        AffinityCallableWrapper(affinity, it)
    })

    override fun <T : Any> invokeAny(
        tasks: MutableCollection<out Callable<T>>,
        timeout: Long,
        unit: TimeUnit,
    ): T = delegate.invokeAny(tasks.map {
        AffinityCallableWrapper(affinity, it)
    }, timeout, unit)
}