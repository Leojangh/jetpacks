@file:Suppress("UNUSED")

package com.genlz.jetpacks.utility.appcompat

import android.os.Binder
import android.os.Bundle
import androidx.core.app.BundleCompat

fun Bundle.getBinderExt(key: String?) = BundleCompat.getBinder(this, key)

fun Bundle.putBinderExt(key: String?, binder: Binder?) = BundleCompat.putBinder(this, key, binder)

