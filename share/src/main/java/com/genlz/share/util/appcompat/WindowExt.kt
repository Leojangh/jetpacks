@file:Suppress("NOTHING_TO_INLINE", "UNUSED") // Aliases to other public API.

package com.genlz.share.util.appcompat

import android.view.Window
import androidx.core.view.WindowCompat

/**
 * @see WindowCompat.setDecorFitsSystemWindows
 */
fun Window.setDecorFitsSystemWindowsExt(fitSystemWindow: Boolean) =
    WindowCompat.setDecorFitsSystemWindows(this, fitSystemWindow)