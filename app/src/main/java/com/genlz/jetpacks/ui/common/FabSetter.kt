package com.genlz.jetpacks.ui.common

import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

fun interface FabSetter {

    fun setupFab(action: FloatingActionButton.() -> Unit)

    companion object {

        /**
         * Every fragment can call this extension method to setup [FloatingActionButton] individually,
         * such as show,hide,icon and actions.
         *
         * Auto clear side effects can't implement by lifecycle temporarily:
         * ```
         * viewLifecycleOwnerLiveData.observe(this) { viewLifecycleOwner ->
         *     viewLifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
         *         override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
         *             if (event == Lifecycle.Event.ON_DESTROY) {
         *                 fabSetter?.setupFab {
         *                     hide()
         *                     setOnClickListener(null)
         *                     setImageDrawable(null)
         *                 }
         *             }
         *         }
         *     })
         * }
         * ```
         * Aka every fragment always see the state of fab set by last fragment,so every fragment
         * should take some actions to clear last state or override them according to requirements.
         * Perhaps there is a better way to implement but I have no idea about it.
         */
        fun Fragment.findFabSetter() = activity as? FabSetter
    }
}