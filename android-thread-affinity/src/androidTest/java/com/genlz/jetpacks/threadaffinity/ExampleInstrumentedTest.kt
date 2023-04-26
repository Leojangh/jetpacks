package com.genlz.jetpacks.threadaffinity

import android.os.HardwarePropertiesManager
import androidx.core.content.getSystemService
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.io.path.Path
import kotlin.io.path.readText

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.genlz.jetpacks.threadaffinity.test", appContext.packageName)

        val process = ProcessBuilder("inotifyd".split("\\s"))
            .inheritIO()
            .start()
//        process.inputStream.bufferedReader().use {
//            assertEquals("a", it.readText())
//        }
        process.errorStream.bufferedReader().use {
            assertEquals("a", it.readText())
        }
//        println(Path("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq").readText())
    }
}