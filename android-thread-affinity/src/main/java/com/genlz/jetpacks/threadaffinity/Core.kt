package com.genlz.jetpacks.threadaffinity

import kotlinx.coroutines.flow.Flow

data class Core(
    val cpuid: Int,
    val maxFreq: Int,
    val minFreq: Int = 0,
    val type: BigLittleType,
) {

    fun curFreq(): Flow<Int> {
        TODO()
    }
}