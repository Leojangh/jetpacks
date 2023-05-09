package com.genlz.jetpacks.libnative

import org.junit.Test
import java.nio.file.Files
import kotlin.io.path.Path

class RustNativesTest {

    @Test
    fun test() {
        val tail = "06 20 ?? 00 91 15 00 00 34 e1 96 00 00 10 00 ??"
        val bytes = Files.readAllBytes(Path("../sampledata/classes.dex"))
        println(bytes.size)
        //We can't call this
//        println(RustNatives.search(bytes, tail))
    }
}