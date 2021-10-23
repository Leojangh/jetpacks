@file:Suppress("UNUSED")

package com.genlz.jetpacks.utility.appcompat

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.IntRange
import androidx.core.app.ActivityCompat
import androidx.core.app.SharedElementCallback
import androidx.core.content.LocusIdCompat

fun Activity.startActivityForResultExt(intent: Intent, requestCode: Int, options: Bundle?) =
    ActivityCompat.startActivityForResult(this, intent, requestCode, options)

fun Activity.startIntentSenderForResultExt(
    intent: IntentSender,
    requestCode: Int,
    fillInIntent: Intent?,
    flagsMask: Int,
    flagsValue: Int,
    extraFlags: Int,
    options: Bundle?
) = ActivityCompat.startIntentSenderForResult(
    this,
    intent,
    requestCode,
    fillInIntent,
    flagsMask,
    flagsValue,
    extraFlags,
    options
)

fun Activity.finishAffinityExt() = ActivityCompat.finishAffinity(this)

fun Activity.finishAfterTransitionExt() = ActivityCompat.finishAfterTransition(this)

fun Activity.getReferrerExt() = ActivityCompat.getReferrer(this)

fun <T : View> Activity.requireViewByIdExt(@IdRes id: Int) =
    ActivityCompat.requireViewById<T>(this, id)

fun Activity.setEnterSharedElementCallbackExt(callback: SharedElementCallback?) =
    ActivityCompat.setEnterSharedElementCallback(this, callback)

fun Activity.setExitSharedElementCallback(callback: SharedElementCallback?) =
    ActivityCompat.setExitSharedElementCallback(this, callback)

fun Activity.postponeEnterTransitionExt() = ActivityCompat.postponeEnterTransition(this)

fun Activity.startPostponedEnterTransitionExt() = ActivityCompat.startPostponedEnterTransition(this)

fun Activity.requestPermissionsExt(
    permissions: Array<String>,
    @IntRange(from = 0) requestCode: Int
) = ActivityCompat.requestPermissions(this, permissions, requestCode)

fun Activity.shouldShowRequestPermissionRationaleExt(permission: String) =
    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)

fun Activity.requestDragAndDropPermissionsExt(dragEvent: DragEvent) =
    ActivityCompat.requestDragAndDropPermissions(this, dragEvent)

fun Activity.recreateExt() = ActivityCompat.recreate(this)

fun Activity.setLocusContextExt(locusId: LocusIdCompat?, bundle: Bundle?) =
    ActivityCompat.setLocusContext(this, locusId, bundle)





