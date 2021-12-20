@file:Suppress("UNUSED", "NOTHING_TO_INLINE")

package com.genlz.share.util.appcompat

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Request the permission and then do the [action].It's okay to just use it to request permissions
 *
 * @param onRejected the action which will be executed on request permission rejected.
 * @param onShowRationale the action which will be executed on show request permission rationale.
 * @param action the action which will be executed after permission granted.
 */
suspend fun ComponentActivity.doWithPermission(
    permission: String,
    onRejected: suspend (String) -> Unit = {},
    onShowRationale: suspend (String) -> Unit = {},
    action: suspend () -> Unit = {},
) = when {
    checkSelfPermissionExt(permission) == PackageManager.PERMISSION_GRANTED -> action()

    shouldShowRequestPermissionRationaleExt(permission) -> onShowRationale(permission)

    else -> {
        val granted =
            registerForActivityResult(ActivityResultContracts.RequestPermission())(permission)
        if (granted) {
            action()
        } else {
            onRejected(permission)
        }
    }
}

/**
 * The **[Activity Result API](https://developer.android.com/training/basics/intents/result#register)** powered by coroutines.
 *
 * @see ActivityResultCaller.registerForActivityResult
 */
suspend fun <I, O> ActivityResultCaller.launchActivityResultContract(
    contract: ActivityResultContract<I, O>,
    registry: ActivityResultRegistry? = null,
    input: I,
    options: ActivityOptionsCompat? = null,
): O = suspendCoroutine { continuation ->
    val launcher = if (registry != null)
        registerForActivityResult(contract, registry) {
            continuation.resume(it)
        }
    else registerForActivityResult(contract) {
        continuation.resume(it)
    }
    launcher.launch(input, options)
}

fun <I, O> ActivityResultCaller.registerForActivityResult(
    contract: ActivityResultContract<I, O>,
    registry: ActivityResultRegistry? = null,
    options: ActivityOptionsCompat? = null,
): suspend (I) -> O = { input ->
    suspendCoroutine { continuation ->
        val launcher = if (registry == null) registerForActivityResult(contract) {
            continuation.resume(it)
        } else registerForActivityResult(contract, registry) {
            continuation.resume(it)
        }
        launcher.launch(input, options)
    }
}

/**
 * Request a bundle of permissions.
 */
suspend fun ComponentActivity.doWithPermissions(
    permissions: Array<String>,
    onRejected: suspend (String) -> Unit = {},
    onShowRationale: suspend (String) -> Unit = {},
    action: suspend () -> Unit = {},
) = when {
    permissions.all { checkSelfPermissionExt(it) == PackageManager.PERMISSION_GRANTED } -> action()
    else -> {
        val grantedResults = launchActivityResultContract(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            input = permissions
        )
        if (grantedResults.all { it.value }) {
            action()
        } else {
            grantedResults.filter {
                !it.value /*filter rejected permissions*/
            }.forEach {
                if (shouldShowRequestPermissionRationaleExt(it.key)) {
                    onShowRationale(it.key)
                } else {
                    onRejected(it.key)
                }
            }
        }
    }
}
