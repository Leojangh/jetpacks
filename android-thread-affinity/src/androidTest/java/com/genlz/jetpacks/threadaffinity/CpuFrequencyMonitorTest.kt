package com.genlz.jetpacks.threadaffinity

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.file.FileSystems

@RunWith(AndroidJUnit4::class)
class CpuFrequencyMonitorTest {

    val appContext = InstrumentationRegistry.getInstrumentation().targetContext


    @Test
    fun test() {
        val newWatchService = FileSystems.getDefault().newWatchService()
        println(newWatchService)
    }
}