@file:Suppress("UNUSED")

package com.genlz.jetpacks.utility.appcompat

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.File
import java.util.concurrent.Executor

val Context.appCompatActivity
    get() = generateSequence(this) {
        if (it is ContextWrapper) {
            it.baseContext
        } else null
    }.firstOrNull { it is AppCompatActivity } as AppCompatActivity?

fun Context.getAttributionTagExt() = ContextCompat.getAttributionTag(this)

fun Context.startActivitiesExt(intents: Array<Intent>, options: Bundle? = null) =
    ContextCompat.startActivities(this, intents, options)

fun Context.startActivityExt(intent: Intent, options: Bundle?) =
    ContextCompat.startActivity(this, intent, options)

fun Context.getDataDirExt() = ContextCompat.getDataDir(this)

fun Context.getObbDirsExt(): Array<File> = ContextCompat.getObbDirs(this)

fun Context.getExternalFilesDirsExt(type: String?): Array<File> =
    ContextCompat.getExternalFilesDirs(this, type)

fun Context.getExternalCacheDirsExt(): Array<File> = ContextCompat.getExternalCacheDirs(this)

fun Context.getDrawableExt(@DrawableRes id: Int) = ContextCompat.getDrawable(this, id)

fun Context.getColorStateListExt(@ColorRes id: Int) = ContextCompat.getColorStateList(this, id)

fun Context.getColorExt(@ColorRes id: Int) = ContextCompat.getColor(this, id)

fun Context.checkSelfPermissionExt(permission: String) =
    ContextCompat.checkSelfPermission(this, permission)

fun Context.getNoBackupFilesDirExt() = ContextCompat.getNoBackupFilesDir(this)

fun Context.getCodeCacheDirExt(): File = ContextCompat.getCodeCacheDir(this)

fun Context.createDeviceProtectedStorageContextExt() =
    ContextCompat.createDeviceProtectedStorageContext(this)

fun Context.isDeviceProtectedStorageExt() = ContextCompat.isDeviceProtectedStorage(this)

fun Context.getMainExecutorExt(): Executor = ContextCompat.getMainExecutor(this)

fun Context.startForegroundServiceExt(intent: Intent) =
    ContextCompat.startForegroundService(this, intent)

/**
 * For instant app,may be null for some service.
 */
fun <T> Context.getSystemServiceExt(serviceClass: Class<T>) =
    ContextCompat.getSystemService(this, serviceClass)
        ?: error("You can't access the service ${serviceClass.canonicalName}")

fun Context.getSystemServiceNameExt(serviceClass: Class<*>) =
    ContextCompat.getSystemServiceName(this, serviceClass)
        ?: error("The service ${serviceClass.canonicalName} is not support!")

