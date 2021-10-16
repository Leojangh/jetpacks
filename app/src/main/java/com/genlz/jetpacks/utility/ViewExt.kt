package com.genlz.jetpacks.utility

import android.view.View
import android.view.ViewGroup
import androidx.annotation.Px
import androidx.core.view.*

inline fun View.updateMargin(
    @Px left: Int = marginLeft,
    @Px top: Int = marginTop,
    @Px right: Int = marginRight,
    @Px bottom: Int = marginBottom
) {
    updateLayoutParams<ViewGroup.MarginLayoutParams> {
        leftMargin = left
        topMargin = top
        rightMargin = right
        bottomMargin = bottom
    }
}