@file:Suppress("UNUSED","NOTHING_TO_INLINE")

package com.genlz.share.util.appcompat

import android.app.Dialog
import android.view.View
import androidx.annotation.IdRes

/**
 * The official version don't support generic and looks like redundant.
 */
inline fun <T : View> Dialog.requireViewByIdExt(@IdRes id: Int): T = findViewById(id)
    ?: throw IllegalArgumentException("ID does not reference a View inside this Dialog")


