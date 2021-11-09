package com.genlz.share.util.appcompat

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

/**
 * Request the permission and then do the [action].
 *
 * @param onRejected the action which will be executed on request permission rejected.
 * @param onShowRationale the action which will be executed on show request permission rationale.
 * @param action the action which will be executed after permission granted.
 */
fun ComponentActivity.doWithPermission(
    permission: String,
    onRejected: () -> Unit = {},
    onShowRationale: () -> Unit = {},
    action: () -> Unit = {},
) = when {
    checkSelfPermissionExt(permission) == PackageManager.PERMISSION_GRANTED -> action()

    shouldShowRequestPermissionRationaleExt(permission) -> onShowRationale()

    else -> registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) action() else onRejected()
    }.launch(permission)
}
