package com.genlz.jetpacks.ui.web

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.StyleRes

class PowerfulWebView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : WebView(context, attrs, defStyleAttr, defStyleRes) {

    init {
        settings.apply {
            javaScriptEnabled = true
        }
        webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                //inject JQuery
                //inject image hook
            }
        }
    }
}