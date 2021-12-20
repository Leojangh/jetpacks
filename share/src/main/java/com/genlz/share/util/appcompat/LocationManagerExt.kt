@file:Suppress("UNUSED", "NOTHING_TO_INLINE")

package com.genlz.share.util.appcompat

import android.Manifest.permission
import android.location.LocationManager
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.core.location.LocationListenerCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.location.LocationRequestCompat
import java.util.concurrent.Executor

@RequiresPermission(anyOf = [permission.ACCESS_COARSE_LOCATION, permission.ACCESS_FINE_LOCATION])
inline fun LocationManager.requestLocationUpdates(
    provider: String,
    locationRequest: LocationRequestCompat,
    listener: LocationListenerCompat,
    looper: Looper,
) = LocationManagerCompat.requestLocationUpdates(
    this,
    provider,
    locationRequest,
    listener,
    looper
)

@RequiresPermission(anyOf = [permission.ACCESS_COARSE_LOCATION, permission.ACCESS_FINE_LOCATION])
inline fun LocationManager.requestLocationUpdates(
    provider: String,
    locationRequest: LocationRequestCompat,
    executor: Executor,
    listener: LocationListenerCompat,
) = LocationManagerCompat.requestLocationUpdates(
    this,
    provider,
    locationRequest,
    executor,
    listener
)