package com.genlz.jetpacks.threadaffinity

import com.genlz.jetpacks.libnative.CppNatives
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import java.util.concurrent.*
import kotlin.coroutines.CoroutineContext

/**
 * A kind of [ExecutorService],the internal thread has thread affinity.
 */
internal interface AffinityExecutorService : ExecutorService, ThreadAffinity

internal interface ThreadAffinity {
    val affinity: IntArray
}

/**
 * Maybe wrong.
 */
internal class AffinityCoroutineDispatcherDecorator(
    override val affinity: IntArray,
    private val delegate: CoroutineDispatcher,
) : CoroutineDispatcher(), ThreadAffinity {
    override fun dispatch(context: CoroutineContext, block: Runnable) =
        delegate.dispatch(context, AffinityRunnableWrapper(affinity, block))
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

internal class AffinityExecutorServiceDecorator(
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