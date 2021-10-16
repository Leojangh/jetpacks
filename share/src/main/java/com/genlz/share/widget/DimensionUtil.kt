package com.genlz.share.widget

import android.util.DisplayMetrics
import android.util.TypedValue

fun Int.toDp(displayMetrics: DisplayMetrics) =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        displayMetrics
    ).toInt()