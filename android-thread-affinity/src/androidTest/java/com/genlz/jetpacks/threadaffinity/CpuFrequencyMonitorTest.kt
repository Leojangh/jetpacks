package com.genlz.jetpacks.threadaffinity

import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.Test
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchEvent
import kotlin.io.path.Path
import kotlin.io.path.readText


class CpuFrequencyMonitorTest {

    val appContext = InstrumentationRegistry.getInstrumentation().targetContext


    @Test
    fun test() {
        val newWatchService = FileSystems.getDefault().newWatchService()
        println(newWatchService)
    }
}