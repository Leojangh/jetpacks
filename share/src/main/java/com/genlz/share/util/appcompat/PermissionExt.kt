package com.genlz.share.util.appcompat

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import org.jetbrains.annotations.Contract
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
        val granted = launchActivityResultContract(
            ActivityResultContracts.RequestPermission(),
            permission
        )
        if (granted) {
            action()
        } else {
            onRejected(permission)
        }
    }
}

/**
 * The **Activity Result API** powered by coroutines.
 *
 * @see ComponentActivity.registerForActivityResult
 */
suspend fun <I, O> ComponentActivity.launchActivityResultContract(
    contract: ActivityResultContract<I, O>,
    input: I
): O = suspendCoroutine { continuation ->
    registerForActivityResult(contract) {
        continuation.resume(it)
    }.launch(input)
}

/**
 * The **Activity Result API** powered by coroutines.
 *
 * @see ComponentActivity.registerForActivityResult
 */
suspend fun <I, O> ComponentActivity.launchActivityResultContract(
    contract: ActivityResultContract<I, O>,
    registry: ActivityResultRegistry,
    input: I
): O = suspendCoroutine { continuation ->
    registerForActivityResult(contract, registry) {
        continuation.resume(it)
    }.launch(input)
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
            ActivityResultContracts.RequestMultiplePermissions(),
            permissions
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
