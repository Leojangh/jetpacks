package com.genlz.jetpacks.threadaffinity

import android.os.Build

internal data class Cpu(
    val cores: List<Core>,
    val s :String = Build.SOC_MANUFACTURER
)