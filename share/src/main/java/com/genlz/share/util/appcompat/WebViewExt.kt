@file:Suppress("NOTHING_TO_INLINE", "UNUSED") // Aliases to other public API.

package com.genlz.share.util.appcompat

import android.webkit.WebView
import androidx.annotation.NonNull
import androidx.annotation.RequiresFeature
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import com.genlz.share.widget.web.PowerfulWebView

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

