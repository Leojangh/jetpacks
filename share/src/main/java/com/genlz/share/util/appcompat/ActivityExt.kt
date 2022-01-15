@file:Suppress("UNUSED", "NOTHING_TO_INLINE")

package com.genlz.share.util.appcompat

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
import java.lang.IllegalArgumentException

/**
 * @see ActivityCompat.startActivityForResult
 */
inline fun Activity.startActivityForResultExt(intent: Intent, requestCode: Int, options: Bundle?) =
    ActivityCompat.startActivityForResult(this, intent, requestCode, options)

/**
 * @see ActivityCompat.startIntentSenderForResult
 */
inline fun Activity.startIntentSenderForResultExt(
    intent: IntentSender,
    requestCode: Int,
    fillInIntent: Intent?,
    flagsMask: Int,
    flagsValue: Int,
    extraFlags: Int,
    options: Bundle?,
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

/**
 * @see ActivityCompat.finishAffinity
 */
inline fun Activity.finishAffinityExt() = ActivityCompat.finishAffinity(this)

/**
 * @see ActivityCompat.finishAfterTransition
 */
inline fun Activity.finishAfterTransitionExt() = ActivityCompat.finishAfterTransition(this)

/**
 * @see ActivityCompat.getReferrer
 */
inline fun Activity.getReferrerExt() = ActivityCompat.getReferrer(this)

/**
 * @see ActivityCompat.requireViewById
 */
inline fun <T : View> Activity.requireViewByIdExt(@IdRes id: Int): T = findViewById(id)
    ?: throw IllegalArgumentException("ID does not reference a View inside this Activity")

/**
 * @see ActivityCompat.setEnterSharedElementCallback
 */
inline fun Activity.setEnterSharedElementCallbackExt(callback: SharedElementCallback?) =
    ActivityCompat.setEnterSharedElementCallback(this, callback)

/**
 * @see ActivityCompat.setExitSharedElementCallback
 */
inline fun Activity.setExitSharedElementCallback(callback: SharedElementCallback?) =
    ActivityCompat.setExitSharedElementCallback(this, callback)

/**
 * @see ActivityCompat.postponeEnterTransition
 */
inline fun Activity.postponeEnterTransitionExt() = ActivityCompat.postponeEnterTransition(this)

/**
 * @see ActivityCompat.startPostponedEnterTransition
 */
inline fun Activity.startPostponedEnterTransitionExt() =
    ActivityCompat.startPostponedEnterTransition(this)

/**
 * @see ActivityCompat.requestPermissions
 */
inline fun Activity.requestPermissionsExt(
    permissions: Array<String>,
    @IntRange(from = 0) requestCode: Int,
) = ActivityCompat.requestPermissions(this, permissions, requestCode)

/**
 * @see ActivityCompat.shouldShowRequestPermissionRationale
 */
inline fun Activity.shouldShowRequestPermissionRationaleExt(permission: String) =
    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)

/**
 * @see ActivityCompat.requestDragAndDropPermissions
 */
inline fun Activity.requestDragAndDropPermissionsExt(dragEvent: DragEvent) =
    ActivityCompat.requestDragAndDropPermissions(this, dragEvent)

/**
 * @see ActivityCompat.recreate
 */
inline fun Activity.recreateExt() = ActivityCompat.recreate(this)

/**
 * @see ActivityCompat.setLocusContext
 */
inline fun Activity.setLocusContextExt(locusId: LocusIdCompat?, bundle: Bundle?) =
    ActivityCompat.setLocusContext(this, locusId, bundle)


/**
 * The [lazy] wrapper without thread safety promise.
 * @see lazy
 */
inline fun <T> lazyNoneSafe(crossinline initializer: () -> T) =
    lazy(LazyThreadSafetyMode.NONE) { initializer() }

