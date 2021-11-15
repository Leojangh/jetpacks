package com.genlz.jetpacks.ui.common

import androidx.fragment.app.Fragment

interface FullscreenController {

    /**
     * Enter fullscreen mode.
     * @param sticky sticky immersive mode or not.
     */
    fun enterFullscreen(sticky: Boolean)

    /**
     * Exit fullscreen mode.
     */
    fun exitFullscreen()

    companion object {

        /**
         * The method depends on the container activity as a [FullscreenController] must
         * implements correctly.
         */
        fun Fragment.findFullscreenController() = activity as? FullscreenController
    }
}