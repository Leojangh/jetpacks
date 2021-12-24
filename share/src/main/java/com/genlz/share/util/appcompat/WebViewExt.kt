@file:Suppress("NOTHING_TO_INLINE", "UNUSED") // Aliases to other public API.

package com.genlz.share.util.appcompat

import android.webkit.WebView
import androidx.annotation.NonNull
import androidx.annotation.RequiresFeature
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * @see WebViewCompat.postVisualStateCallback
 */
@RequiresFeature(
    name = WebViewFeature.VISUAL_STATE_CALLBACK,
    enforcement = "androidx.webkit.WebViewFeature#isFeatureSupported"
)
inline fun WebView.postVisualStateCallback(
    requestId: Long,
    @NonNull callback: WebViewCompat.VisualStateCallback
) {
    WebViewCompat.postVisualStateCallback(this, requestId, callback)
}

/**
 * Convert the callback style to kotlin coroutine.
 */
suspend fun WebView.evaluateJavascript(script: String): String =
    suspendCoroutine { cont -> evaluateJavascript(script) { cont.resume(it) } }

