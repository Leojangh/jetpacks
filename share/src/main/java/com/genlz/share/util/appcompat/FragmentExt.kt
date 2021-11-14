@file:Suppress("UNUSED", "NOTHING_TO_INLINE")

package com.genlz.share.util.appcompat

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment


inline fun <reified T : Activity> Fragment.startActivity(options: Bundle? = null) {
    startActivity(requireContext().intent<T>(), options)
}