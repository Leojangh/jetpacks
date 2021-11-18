@file:Suppress("NOTHING_TO_INLINE", "UNUSED") // Aliases to other public API.

package com.genlz.share.util.appcompat

import android.view.Window
import androidx.core.view.WindowCompat

/**
 * Tell the Window that our app is going to responsible for fitting for any system windows.
 * This is similar to the now deprecated:
 * view.setSystemUiVisibility(LAYOUT_STABLE | LAYOUT_FULLSCREEN | LAYOUT_HIDE_NAVIGATION)
 *
 * @see WindowCompat.setDecorFitsSystemWindows
 */
inline fun Window.setDecorFitsSystemWindowsExt(fitSystemWindow: Boolean) =
    WindowCompat.setDecorFitsSystemWindows(this, fitSystemWindow)