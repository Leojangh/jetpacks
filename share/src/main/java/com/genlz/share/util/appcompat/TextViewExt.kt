@file:Suppress("NOTHING_TO_INLINE", "UNUSED") // Aliases to other public API.

package com.genlz.share.util.appcompat

import android.graphics.drawable.Drawable
import android.widget.TextView

/**
 * A extension for named and default parameter.
 */
inline fun TextView.setCompoundDrawablesExt(
    left: Drawable? = compoundDrawables[0],
    top: Drawable? = compoundDrawables[1],
    right: Drawable? = compoundDrawables[2],
    bottom: Drawable? = compoundDrawables[3]
) = setCompoundDrawables(left, top, right, bottom)
