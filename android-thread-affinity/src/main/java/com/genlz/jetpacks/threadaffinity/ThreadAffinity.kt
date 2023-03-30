package com.genlz.jetpacks.threadaffinity

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.Size
import com.genlz.jetpacks.libnative.CppNatives
import kotlinx.coroutines.CoroutineDispatcher
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.ForkJoinTask
import java.util.concurrent.Future
import java.util.concurrent.RecursiveAction
import java.util.concurrent.RecursiveTask
import java.util.concurrent.RejectedExecutionHandler
import java.util.concurrent.RunnableFuture
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.function.Predicate
import kotlin.coroutines.CoroutineContext

/**
 * A kind of [ExecutorService],the internal thread has thread affinity.
 */
internal interface AffinityExecutorService : ExecutorService, ThreadAffinity {
    //Builder flavor
    fun setAffinity(affinity: IntArray): AffinityExecutorService {
        this.affinity = affinity
        return this
    }
}

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
        setAffinityNative()
        command.run()
    }
}

@JvmDefaultWithoutCompatibility
internal interface AbsAffinityWrapper : ThreadAffinity {
    fun setAffinityNative() {
        CppNatives.setAffinity(cpus = affinity)
    }
}

internal class AffinityRecursiveTask<V>(
    @Size(min = 1)
    override var affinity: IntArray,
    private val task: RecursiveTask<V>,
) : RecursiveTask<V>(), AbsAffinityWrapper {

    companion object {
        private val f = RecursiveTask::class.java.getDeclaredMethod("compute")
    }

    override fun compute(): V {
        setAffinityNative()
        @Suppress("UNCHECKED_CAST")
        return f(task) as V
    }
}

internal class AffinityRecursiveAction(
    @Size(min = 1)
    override var affinity: IntArray,
    private val action: RecursiveAction,
) : RecursiveAction(), AbsAffinityWrapper {
    companion object {
        private val f = RecursiveAction::class.java.getDeclaredMethod("compute")
    }

    override fun compute() {
        setAffinityNative()
        f(action)
    }
}

//CountedCompleter

internal class AffinityCallableWrapper<V>(
    @Size(min = 1)
    override var affinity: IntArray,
    private val task: Callable<V>,
) : Callable<V>, AbsAffinityWrapper {
    override fun call(): V {
        setAffinityNative()
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
    if (affinity.isEmpty()) throw IllegalArgumentException("affinity is empty.") else ThreadFactory {
        threadFactory.newThread(AffinityRunnableWrapper(affinity, it))
    },
    handler
), AffinityExecutorService

internal class AffinityForkJoinPool : ForkJoinPool, AffinityExecutorService {

    override var affinity: IntArray

    constructor(@Size(min = 1) affinity: IntArray) : super(affinity.size) {
        this.affinity = affinity
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    constructor(
        @Size(min = 1)
        affinity: IntArray,
        factory: ForkJoinWorkerThreadFactory = defaultForkJoinWorkerThreadFactory,
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
    ) : super(
        affinity.size,
        factory,
        handler,
        asyncMode,
        corePoolSize,
        maximumPoolSize,
        minimumRunnable,
        saturate,
        keepAliveTime,
        unit
    ) {
        this.affinity = affinity
    }

    override fun <T> invoke(task: ForkJoinTask<T>): T {
        require(task is RecursiveTask)
        return super.invoke(AffinityRecursiveTask(affinity, task))
    }

    override fun execute(task: ForkJoinTask<*>) {
        require(task is RecursiveAction)
        return super.execute(AffinityRecursiveAction(affinity, task))
    }

    override fun execute(task: Runnable) =
        super.execute(AffinityRunnableWrapper(affinity, task))

    override fun <T> submit(task: ForkJoinTask<T>): ForkJoinTask<T> {
        require(task is RecursiveTask)
        return super.submit(AffinityRecursiveTask(affinity, task))
    }

    override fun <T> submit(task: Callable<T>): ForkJoinTask<T> =
        super.submit(AffinityCallableWrapper(affinity, task))

    override fun <T> submit(task: Runnable, result: T): ForkJoinTask<T> =
        super.submit(AffinityRunnableWrapper(affinity, task), result)

    override fun submit(task: Runnable): ForkJoinTask<*> =
        super.submit(AffinityRunnableWrapper(affinity, task))

    override fun <T> invokeAll(tasks: Collection<Callable<T>>): List<Future<T>> =
        super.invokeAll(tasks.map { AffinityCallableWrapper(affinity, it) })

    //no need delegate
    override fun <T> newTaskFor(runnable: Runnable, value: T): RunnableFuture<T> =
        super.newTaskFor(AffinityRunnableWrapper(affinity, runnable), value)

    //no need delegate
    override fun <T> newTaskFor(callable: Callable<T>): RunnableFuture<T> =
        super.newTaskFor(AffinityCallableWrapper(affinity, callable))
}

internal class AffinityExecutorServiceDecorator(
    @Size(min = 1)
    override var affinity: IntArray,
    internal val delegate: ExecutorService,
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