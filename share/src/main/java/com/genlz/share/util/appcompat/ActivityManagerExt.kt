@file:Suppress("UNUSED", "NOTHING_TO_INLINE")

package com.genlz.share.util.appcompat

import android.app.ActivityManager
import androidx.core.app.ActivityManagerCompat

/**
 * @see ActivityManagerCompat.isLowRamDevice
 */
inline val ActivityManager.isLowRamDeviceExt
    get() = ActivityManagerCompat.isLowRamDevice(this)

/**
 * @see ActivityManager.getMemoryInfo
 */
inline fun ActivityManager.getMemoryInfo(): ActivityManager.MemoryInfo {
    return ActivityManager.MemoryInfo().also(this::getMemoryInfo)
}