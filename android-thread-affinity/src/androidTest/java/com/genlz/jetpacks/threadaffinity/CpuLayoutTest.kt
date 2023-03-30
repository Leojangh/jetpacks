package com.genlz.jetpacks.threadaffinity

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CpuLayoutTest {

    @Test
    fun a() {
        println(CpuLayout.cores())
    }
}