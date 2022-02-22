package com.genlz.share.util

import android.content.Intent

/**
 * Convenience calling for [Intent.setClassName].
 */
fun Intent(packageName: String, className: String): Intent {
    return Intent().setClassName(packageName, className)
}