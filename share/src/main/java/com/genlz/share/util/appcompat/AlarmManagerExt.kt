@file:Suppress("UNUSED", "NOTHING_TO_INLINE")

package com.genlz.share.util.appcompat

import android.app.AlarmManager
import android.app.PendingIntent
import androidx.core.app.AlarmManagerCompat

/**
 * @see AlarmManagerCompat.setAlarmClock
 */
inline fun AlarmManager.setAlarmClockExt(
    triggerTime: Long,
    showIntent: PendingIntent,
    operation: PendingIntent
) = AlarmManagerCompat.setAlarmClock(this, triggerTime, showIntent, operation)

/**
 * @see AlarmManagerCompat.setAndAllowWhileIdle
 */
inline fun AlarmManager.setAndAllowWhileIdleExt(
    type: Int,
    triggerAtMillis: Long,
    operation: PendingIntent
) = AlarmManagerCompat.setAndAllowWhileIdle(this, type, triggerAtMillis, operation)

/**
 * @see AlarmManagerCompat.setExact
 */
inline fun AlarmManager.setExactExt(type: Int, triggerAtMillis: Long, operation: PendingIntent) =
    AlarmManagerCompat.setExact(this, type, triggerAtMillis, operation)

/**
 * @see AlarmManagerCompat.setExactAndAllowWhileIdle
 */
inline fun AlarmManager.setExactAndAllowWhileIdleExt(
    type: Int,
    triggerAtMillis: Long,
    operation: PendingIntent
) = AlarmManagerCompat.setExactAndAllowWhileIdle(this, type, triggerAtMillis, operation)




