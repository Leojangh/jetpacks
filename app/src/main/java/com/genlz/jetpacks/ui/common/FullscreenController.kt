package com.genlz.jetpacks.ui.common

import androidx.fragment.app.Fragment

interface FullscreenController {

    fun enterFullscreen()

    fun exitFullscreen()

    companion object {
        fun Fragment.findFullscreenController() = activity as? FullscreenController
    }
}