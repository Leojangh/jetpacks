@file:Suppress("UNUSED", "NOTHING_TO_INLINE")

package com.genlz.share.util

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import com.genlz.share.util.appcompat.postponeEnterTransitionExt
import com.genlz.share.util.appcompat.startPostponedEnterTransitionExt

/**
 * The two fragments involved in shared elements transition animation
 * both need to wait view ready.Or you will see nothing about transition
 * because the shared elements animation have played without views.
 *
 * <b>Note:</b>Call this at [Fragment.onViewCreated].
 */
inline fun Fragment.postponeEnterTransitionUtilDraw() {
    (requireView().parent as? ViewGroup)?.doOnPreDraw {
        startPostponedEnterTransition()
    }
    postponeEnterTransition()
}

/**
 * @see postponeEnterTransitionUtilDraw
 */
inline fun Activity.postponeEnterTransitionUtilDraw() {
    window.findViewById<View>(Window.ID_ANDROID_CONTENT).doOnPreDraw {
        startPostponedEnterTransitionExt()
    }
    postponeEnterTransitionExt()
}