package com.genlz.share.widget.web

import android.graphics.RectF
import android.webkit.WebView

fun interface DomTouchListener {

    /**
     * The dom click listener.
     *
     * @param webView the host web view.
     * @param hitTestResult the hit test result.
     * @param rectF the child dom precise position in the parent,aka the [webView].
     */
    fun onDomTouch(webView: WebView, hitTestResult: WebView.HitTestResult, rectF: RectF)
}