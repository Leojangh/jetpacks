package com.genlz.jetpacks.ui.web.bridge

import android.graphics.RectF
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import com.genlz.javascript.Android
import com.genlz.share.util.appcompat.mainExecutorExt
import com.genlz.share.widget.web.PowerfulWebView

class AndroidImpl(
    private val webView: WebView
) : Android {

    /**
     * The context associate with [webView]
     */
    private val context = webView.context

    /**
     * **Note**:The name is super type name.
     */
    val name: String = Android::class.java.simpleName

    @JavascriptInterface
    override fun toast(message: String) {
        context.mainExecutorExt.execute {
            Toast.makeText(context.applicationContext, message, Toast.LENGTH_SHORT).show()
        }
    }

    @JavascriptInterface
    override fun transit(left: Float, top: Float, right: Float, bottom: Float) {
        if (webView !is PowerfulWebView) {
            Log.w(TAG, UnsupportedOperationException())
            return
        }
        //Not post attention because web view has it's own looper.
        context.mainExecutorExt.execute {
            val r = RectF(left, top, right, bottom)
            webView.domTouchListener?.onDomTouch(webView, webView.hitTestResult, r)
        }
    }

    companion object {
        private const val TAG = "AndroidImpl"
    }
}