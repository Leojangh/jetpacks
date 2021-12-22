package com.genlz.jetpacks

import com.genlz.share.util.getValue
import com.genlz.share.util.setValue
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    private val atomicInteger = AtomicInteger(0)

    private var atomicValue by atomicInteger

    private val atomicString = AtomicReference("")
    private var string by atomicString

    @Test
    fun addition_isCorrect() {
        atomicValue++
        assertEquals(1, atomicInteger.get())
        assertEquals(1, atomicValue)
        assertEquals(4, atomicValue + 3)
        assertEquals(1, atomicInteger.get())
        string = "sfs"
        assertEquals("sfs", atomicString.get())
        assertEquals("sfs", string)
    }
}