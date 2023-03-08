package com.genlz.jetpacks.threadaffinity

import android.util.SparseIntArray
import androidx.core.util.forEach
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.readText

internal class CpuLayoutImpl : CpuLayout {

    companion object {

        private val CORES = Runtime.getRuntime().availableProcessors()

        private val cores = detect()

        private fun getCpuInfoPath(cpuid: Int) =
            Path("/sys/devices/system/cpu/cpu$cpuid/cpufreq/cpuinfo_max_freq")

        /**
         * 通过读取每个核心的最大频率来进行区分
         */
        private fun detect(): List<Core> {
            val cpuid2freq = SparseIntArray(CORES).apply {
                repeat(CORES) {
                    put(it, getCpuInfoPath(it).readText().trim().toInt())
                }
            }
            val freq2cpuIds = TreeMap<Int, MutableList<Int>>()
            cpuid2freq.forEach { id, freq ->
                freq2cpuIds.merge(freq, mutableListOf(id)) { old, value ->
                    old.addAll(value)
                    old
                }
            }
            val cores = mutableListOf<Core>()
            when (freq2cpuIds.size) {
                1 -> repeat(CORES) {
                    cores += Core(cpuid = it, maxFreq = cpuid2freq[0], type = BigLittleType.BIG)
                } //通用
                2 -> {
                    val iterator = freq2cpuIds.iterator()
                    val (littleFreq, littleIds) = iterator.next()
                    littleIds.forEach {
                        cores += Core(cpuid = it, maxFreq = littleFreq, type = BigLittleType.LITTLE)
                    }
                    val (bigFreq, bigIds) = iterator.next()
                    bigIds.forEach {
                        cores += Core(cpuid = it, maxFreq = bigFreq, type = BigLittleType.BIG)
                    }
                }//大小核
                3 -> {
                    val iterator = freq2cpuIds.iterator()
                    val (littleFreq, littleIds) = iterator.next()
                    littleIds.forEach {
                        cores += Core(cpuid = it, maxFreq = littleFreq, type = BigLittleType.LITTLE)
                    }
                    val (bigFreq, bigIds) = iterator.next()
                    bigIds.forEach {
                        cores += Core(cpuid = it, maxFreq = bigFreq, type = BigLittleType.BIG)
                    }
                    val (primeFreq, primeIds) = iterator.next()
                    primeIds.forEach {
                        cores += Core(cpuid = it, maxFreq = primeFreq, type = BigLittleType.PRIME)
                    }
                }//超大核、中核、小核
                else -> error("Unsupported")
            }
            return cores
        }
    }

    override fun bigCores() = cores
        .filter { it.type == BigLittleType.BIG }
        .map { it.cpuid }.toIntArray()

    override fun littleCores() = cores
        .filter { it.type == BigLittleType.LITTLE }
        .map { it.cpuid }.toIntArray()

    override fun primeCores() = cores
        .filter { it.type == BigLittleType.PRIME }
        .map { it.cpuid }.toIntArray()

    override fun cores() = cores
}