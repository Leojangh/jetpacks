package com.genlz.jetpacks.ui.common

import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBar.LayoutParams.MATCH_PARENT
import androidx.fragment.app.Fragment

fun interface ActionBarCustomizer {

    fun custom(customizer: ActionBar.() -> Unit)

    companion object {
        fun Fragment.findActionBarCustomizer(): ActionBarCustomizer? {
            return activity as? ActionBarCustomizer
        }

        /**
         * [androidx.appcompat.app.ToolbarActionBar.setCustomView] use default
         * layout params [MATCH_PARENT].So the match_parent is invalidate
         * in custom layout,unless it becomes the direct subview of action bar,such as a child of Toolbar:
         * ...
         * <Toolbar>
         *     <include layout="@layout/layoutId"/>
         * </Toolbar>
         * ...
         * You can access the inflated view by [ActionBar.getCustomView].
         */
        fun ActionBar.setCustomViewFitAllSpace(@LayoutRes layoutId: Int) {
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            val v = View.inflate(themedContext, layoutId, null)
            setCustomView(
                v,
                ActionBar.LayoutParams(
                    MATCH_PARENT,
                    MATCH_PARENT
                )
            )
        }
    }
}