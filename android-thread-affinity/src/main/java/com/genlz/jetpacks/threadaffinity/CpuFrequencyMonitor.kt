package com.genlz.jetpacks.threadaffinity

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class CpuFrequencyMonitor {

    fun poll(cpu: Int, interval: Duration = 1.toDuration(DurationUnit.SECONDS)): Flow<Int> {
        require(cpu in 0 until Runtime.getRuntime().availableProcessors())
        return flow {
            while (currentCoroutineContext().isActive) {
                emit(
                    Path("/sys/devices/system/cpu/cpu$cpu/cpufreq/scaling_cur_freq").readText()
                        .trim()
                        .toInt()
                )
                delay(interval)
            }
        }.flowOn(Dispatchers.IO)
    }
}