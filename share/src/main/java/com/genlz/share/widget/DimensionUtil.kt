package com.genlz.share.widget

import android.util.DisplayMetrics
import android.util.TypedValue

@JvmInline
value class Dp(val value: Int)

inline val Int.dp get() = Dp(this)

/**
 * Return pixel dimension of unit dp.
 */
fun Int.toDp(displayMetrics: DisplayMetrics) =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        displayMetrics
    ).toInt()