package com.genlz.jetpacks.threadaffinity

import androidx.annotation.Size
import com.genlz.jetpacks.libnative.CppNatives
import kotlinx.coroutines.CoroutineDispatcher
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.RejectedExecutionHandler
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

/**
 * A kind of [ExecutorService],the internal thread has thread affinity.
 */
internal interface AffinityExecutorService : ExecutorService, ThreadAffinity

internal interface ThreadAffinity {
    @set:Size(min = 1)
    @get:Size(min = 1)
    var affinity: IntArray
}

/**
 * Maybe wrong.
 *
 * bug:Invalid argument
 */
internal class AffinityCoroutineDispatcherDecorator(
    @Size(min = 1)
    override var affinity: IntArray,
    private val delegate: CoroutineDispatcher,
) : CoroutineDispatcher(), ThreadAffinity {
    override fun dispatch(context: CoroutineContext, block: Runnable) =
        delegate.dispatch(context, AffinityRunnableWrapper(affinity, block))
}

internal class AffinityRunnableWrapper(
    @Size(min = 1)
    override var affinity: IntArray,
    private val command: Runnable,
) : Runnable, AbsAffinityWrapper {
    override fun run() {
        setAffinity()
        command.run()
    }
}

@JvmDefaultWithoutCompatibility
internal interface AbsAffinityWrapper : ThreadAffinity {
    fun setAffinity() {
        CppNatives.setAffinity(cpus = affinity)
    }
}

internal class AffinityCallableWrapper<V>(
    @Size(min = 1)
    override var affinity: IntArray,
    private val task: Callable<V>,
) : Callable<V>, AbsAffinityWrapper {
    override fun call(): V {
        setAffinity()
        return task.call()
    }
}

internal class AffinityThreadPoolExecutor(
    @Size(min = 1)
    override var affinity: IntArray,
    corePoolSize: Int,
    maximumPoolSize: Int,
    keepAliveTime: Long,
    unit: TimeUnit,
    workQueue: BlockingQueue<Runnable>,
    threadFactory: ThreadFactory,
    handler: RejectedExecutionHandler,
) : ThreadPoolExecutor(
    corePoolSize,
    maximumPoolSize,
    keepAliveTime,
    unit,
    workQueue,
    if (affinity.isEmpty()) threadFactory else ThreadFactory {
        threadFactory.newThread(AffinityRunnableWrapper(affinity, it))
    },
    handler
), AffinityExecutorService

internal class AffinityExecutorServiceDecorator(
    @Size(min = 1)
    override var affinity: IntArray,
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