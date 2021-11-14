package com.genlz.share.widget.web

import android.graphics.RectF
import android.webkit.WebView

fun interface DomTouchListener {

    /**
     * The dom click listener.
     *
     * @param webView the host web view.
     * @param hitTestResult the hit test result.
     * @param rectF the precise position on the device screen.
     */
    fun onDomTouch(webView: WebView, hitTestResult: WebView.HitTestResult, rectF: RectF)
}