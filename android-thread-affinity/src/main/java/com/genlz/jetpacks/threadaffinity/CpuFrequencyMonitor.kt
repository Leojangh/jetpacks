package com.genlz.jetpacks.threadaffinity

import android.annotation.SuppressLint
import android.content.Context
import android.os.*
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService

//TODO not working.
@RequiresApi(Build.VERSION_CODES.Q)
class CpuFrequencyMonitor private constructor(private val ctx: Context) {

    private val s = ctx.getSystemService<HardwarePropertiesManager>()

    companion object {

        private const val TAG = "MainActivity"

        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: CpuFrequencyMonitor? = null
        fun getInstance(ctx: Context): CpuFrequencyMonitor {
            if (null == INSTANCE) {
                synchronized(CpuFrequencyMonitor::class.java) {
                    if (null == INSTANCE) INSTANCE = CpuFrequencyMonitor(ctx.applicationContext)
                }
            }
            return INSTANCE!!
        }
    }
}