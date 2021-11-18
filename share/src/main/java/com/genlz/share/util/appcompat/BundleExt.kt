@file:Suppress("UNUSED", "NOTHING_TO_INLINE")

package com.genlz.share.util.appcompat

import android.os.Binder
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

