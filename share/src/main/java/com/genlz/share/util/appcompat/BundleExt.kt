@file:Suppress("UNUSED", "NOTHING_TO_INLINE")

package com.genlz.share.util.appcompat

import android.os.Binder
import android.os.Build
import android.os.Bundle
import androidx.core.app.BundleCompat

/**
 * @see BundleCompat.getBinder
 */
inline fun Bundle.getBinderExt(key: String?) = BundleCompat.getBinder(this, key)

/**
 * @see BundleCompat.putBinder
 */
inline fun Bundle.putBinderExt(key: String?, binder: Binder?) =
    BundleCompat.putBinder(this, key, binder)

/**
 * We are waiting for [BundleCompat] integrating this method.
 * @see Bundle.getSerializable
 */
inline fun <reified T : java.io.Serializable> Bundle.getSerializableExt(
    key: String?,
): T? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    getSerializable(key, T::class.java)
} else {
    @Suppress("DEPRECATION")
    T::class.java.cast(getSerializable(key))
}

