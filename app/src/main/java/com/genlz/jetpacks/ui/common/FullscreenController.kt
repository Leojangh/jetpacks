package com.genlz.jetpacks.ui.common

import androidx.fragment.app.Fragment

interface FullscreenController {

    /**
     * Enter fullscreen mode.
     * @param sticky sticky immersive mode or not.
     */
    fun enterFullscreen(sticky: Boolean)

    fun exitFullscreen()

    companion object {
        fun Fragment.findFullscreenController() = activity as? FullscreenController
    }
}