@file:Suppress("UNUSED")

package com.genlz.jetpacks.utility.appcompat

import android.app.ActivityManager
import androidx.core.app.ActivityManagerCompat

val ActivityManager.isLowRamDeviceExt
    get() = ActivityManagerCompat.isLowRamDevice(this)