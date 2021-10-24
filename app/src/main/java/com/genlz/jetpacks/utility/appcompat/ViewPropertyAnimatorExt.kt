@file:Suppress("UNUSED", "NOTHING_TO_INLINE")

package com.genlz.jetpacks.utility.appcompat

import android.view.View
import androidx.core.view.ViewPropertyAnimatorCompat
import androidx.core.view.ViewPropertyAnimatorListener

/**
 * Pseudo-Multi listener implementation,that is it,because the
 * framework not support.
 */
private object Listener : ViewPropertyAnimatorListener {

    val startActions: MutableList<(View) -> Unit> = ArrayList(2)
    val endActions: MutableList<(View) -> Unit> = ArrayList(2)
    val cancelActions: MutableList<(View) -> Unit> = ArrayList(2)

    override fun onAnimationCancel(view: View) {
        cancelActions.forEach { action ->
            action(view)
        }
    }

    override fun onAnimationEnd(view: View) {
        endActions.forEach { action ->
            action(view)
        }
    }

    override fun onAnimationStart(view: View) {
        startActions.forEach { action ->
            action(view)
        }
    }
}

/**
 * Add an action which will be invoked when the animation has started.
 */
fun ViewPropertyAnimatorCompat.doOnStart(action: (View) -> Unit): ViewPropertyAnimatorCompat {
    Listener.startActions.add(0, action)
    setListener(Listener)
    return this
}

/**
 * Add an action which will be invoked when the animation has ended.
 */
fun ViewPropertyAnimatorCompat.doOnEnd(action: (View) -> Unit): ViewPropertyAnimatorCompat {
    Listener.endActions += action
    setListener(Listener)
    return this
}

/**
 * Add an action which will be invoked when the animation has cancelled.
 */
fun ViewPropertyAnimatorCompat.doOnCancel(action: (View) -> Unit): ViewPropertyAnimatorCompat {
    Listener.cancelActions += action
    setListener(Listener)
    return this
}