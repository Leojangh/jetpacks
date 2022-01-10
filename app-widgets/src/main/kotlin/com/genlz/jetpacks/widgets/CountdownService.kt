package com.genlz.jetpacks.widgets

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import com.genlz.share.util.appcompat.getSystemService

class CountdownService : Service() {

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        getSystemService<AlarmManager>().set(AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + 1000L,
            @Suppress("WrongConstant")
            PendingIntent.getBroadcast(this,
                0,
                Intent(this, javaClass),
                PendingIntent.FLAG_UPDATE_CURRENT or /*FLAG_IMMUTABLE*/0x4000000))
        return super.onStartCommand(intent, flags, startId)
    }
}