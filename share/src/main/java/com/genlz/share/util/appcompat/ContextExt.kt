@file:Suppress("UNUSED", "NOTHING_TO_INLINE")

package com.genlz.share.util.appcompat

import android.app.Activity
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.File
import java.util.concurrent.Executor

val Context.appCompatActivity
    get() = unwrap() as? AppCompatActivity

/**
 * Try to find a Activity context from this context.
 */
fun Context.unwrap() = generateSequence(this) {
    if (it is ContextWrapper) {
        it.baseContext
    } else null
}.firstOrNull { it is Activity } as? Activity

fun Context.wrap(): Context =
    object : ContextWrapper(this) {
        //inject at here.
    }

inline fun <reified T : Activity> Context.startActivityExt(
    intent: Intent = intent<T>(),
    options: Bundle? = null
) = ContextCompat.startActivity(this, intent, options)

/**
 * Generate a Intent that contains [extras].
 */
inline fun <reified T> Context.intent(extras: Bundle? = null) = Intent(
    this,
    T::class.java
).apply {
    if (extras != null) putExtras(extras)
}

inline fun <reified T> Context.intent(action: String, uri: Uri) =
    Intent(action, uri, this, T::class.java)

inline fun Context.getAttributionTagExt() = ContextCompat.getAttributionTag(this)

inline fun Context.startActivitiesExt(intents: Array<Intent>, options: Bundle? = null) =
    ContextCompat.startActivities(this, intents, options)

inline fun Context.getDataDirExt() = ContextCompat.getDataDir(this)

inline fun Context.getObbDirsExt(): Array<File> = ContextCompat.getObbDirs(this)

inline fun Context.getExternalFilesDirsExt(type: String?): Array<File> =
    ContextCompat.getExternalFilesDirs(this, type)

inline fun Context.getExternalCacheDirsExt(): Array<File> = ContextCompat.getExternalCacheDirs(this)

inline fun Context.getDrawableExt(@DrawableRes id: Int) = ContextCompat.getDrawable(this, id)

inline fun Context.getColorStateListExt(@ColorRes id: Int) =
    ContextCompat.getColorStateList(this, id)

inline fun Context.getColorExt(@ColorRes id: Int) = ContextCompat.getColor(this, id)

inline fun Context.checkSelfPermissionExt(permission: String) =
    ContextCompat.checkSelfPermission(this, permission)

inline fun Context.getNoBackupFilesDirExt() = ContextCompat.getNoBackupFilesDir(this)

inline fun Context.getCodeCacheDirExt(): File = ContextCompat.getCodeCacheDir(this)

inline fun Context.createDeviceProtectedStorageContextExt() =
    ContextCompat.createDeviceProtectedStorageContext(this)

inline fun Context.isDeviceProtectedStorageExt() = ContextCompat.isDeviceProtectedStorage(this)

inline val Context.mainExecutorExt: Executor get() = ContextCompat.getMainExecutor(this)

inline fun Context.startForegroundServiceExt(intent: Intent) =
    ContextCompat.startForegroundService(this, intent)

/**
 * For instant app,may be null for some service,use [androidx.core.content.getSystemService] instead.
 */
inline fun <reified T> Context.getSystemService() =
    ContextCompat.getSystemService(this, T::class.java)
        ?: error("You can't access the service ${T::class.java.canonicalName}")

inline fun Context.getSystemServiceNameExt(serviceClass: Class<*>) =
    ContextCompat.getSystemServiceName(this, serviceClass)
        ?: error("The service ${serviceClass.canonicalName} is not support!")

