@file:Suppress("NOTHING_TO_INLINE", "UNUSED") // Aliases to other public API.

package com.genlz.jetpacks.utility.appcompat

import android.view.Window
import androidx.core.view.WindowCompat

/**
 * @see WindowCompat.setDecorFitsSystemWindows
 */
fun Window.setDecorFitsSystemWindowsExt(fitSystemWindow: Boolean) =
    WindowCompat.setDecorFitsSystemWindows(this, fitSystemWindow)