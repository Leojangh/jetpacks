@file:Suppress("UNUSED")

package com.genlz.share.util.appcompat

import android.app.ActivityManager
import androidx.core.app.ActivityManagerCompat

val ActivityManager.isLowRamDeviceExt
    get() = ActivityManagerCompat.isLowRamDevice(this)

fun ActivityManager.getMemoryInfo(): ActivityManager.MemoryInfo {
    return ActivityManager.MemoryInfo().also(this::getMemoryInfo)
}