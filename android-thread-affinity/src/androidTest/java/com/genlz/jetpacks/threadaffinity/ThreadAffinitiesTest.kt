package com.genlz.jetpacks.threadaffinity

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.genlz.jetpacks.libnative.CppNatives
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Random
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveTask
import java.util.function.IntPredicate

@RunWith(AndroidJUnit4::class)
internal class ThreadAffinitiesTest {

    private val pool = ForkJoinPool()

    private val values = Random().ints(500, 0, 10).toArray()

    class Counter(
        private val range: IntRange,
        private val values: IntArray,
        private val filter: IntPredicate,
    ) : RecursiveTask<Long>() {
        override fun compute(): Long {
            return if (range.last - range.first + 2 <= 100) {
                var count = 0L
                for (i in range) {
                    Thread.sleep(50L)
                    if (filter.test(values[i])) count++
                }
                println("compute $range on cpu:${CppNatives.whereAmIRunning()}")
                count
            } else {
                val mid = (range.first + range.last + 1) / 2
                val c1 = Counter(range.first until mid, values, filter)
                val c2 = Counter(mid..range.last, values, filter)
                invokeAll(c1, c2)
                c1.join() + c2.join()
            }
        }
    }

    @Test
    fun testAffinityRecursiveTask() {
//        val pool = pool.affiliate(CpuLayout.bigCores())
        ThreadAffinities.newAffinityForkJoinPool(CpuLayout.bigCores())
        val counter = Counter(values.indices, values) { it >= 5 }
        println(pool.invoke(counter))
    }
}