package com.genlz.jetpacks.libnative

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.io.path.Path
import kotlin.io.path.readBytes

@RunWith(AndroidJUnit4::class)
class RustNativesTest {

    val ctx = InstrumentationRegistry.getInstrumentation().context

    @Test
    fun test() {
        val tail = "06 20 ?? 00 91 15 00 00 34 e1 96 00 00 10 00 ??"
        val bytes = Path(
            "/data/data/com.genlz.jetpacks.libnative.test/files",
//            "classes.dex"
        ).readBytes()
        println(RustNatives.search(bytes, tail))
    }
}