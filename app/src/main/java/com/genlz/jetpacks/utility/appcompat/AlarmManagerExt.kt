@file:Suppress("UNUSED")

package com.genlz.jetpacks.utility.appcompat

import android.app.AlarmManager
import android.app.PendingIntent
import androidx.core.app.AlarmManagerCompat

fun AlarmManager.setAlarmClockExt(
    triggerTime: Long,
    showIntent: PendingIntent,
    operation: PendingIntent
) = AlarmManagerCompat.setAlarmClock(this, triggerTime, showIntent, operation)

fun AlarmManager.setAndAllowWhileIdleExt(
    type: Int,
    triggerAtMillis: Long,
    operation: PendingIntent
) = AlarmManagerCompat.setAndAllowWhileIdle(this, type, triggerAtMillis, operation)

fun AlarmManager.setExactExt(type: Int, triggerAtMillis: Long, operation: PendingIntent) =
    AlarmManagerCompat.setExact(this, type, triggerAtMillis, operation)

fun AlarmManager.setExactAndAllowWhileIdleExt(
    type: Int,
    triggerAtMillis: Long,
    operation: PendingIntent
) = AlarmManagerCompat.setExactAndAllowWhileIdle(this, type, triggerAtMillis, operation)




