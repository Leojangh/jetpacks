package com.genlz.jetpacks.ui.web.bridge

import android.content.Context
import android.graphics.RectF
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import androidx.annotation.Keep
import com.genlz.jetpacks.ui.web.bridge.Android.Companion.TAG
import com.genlz.share.util.appcompat.mainExecutorExt
import com.genlz.share.widget.web.PowerfulWebView


/**
 * All method run in a dedicate thread named "JavaBridge".So notice the thread context.
 */
@Suppress("UNUSED")
@Keep
interface Android {

    val webView: WebView

    val name: String

    /**
     * The context associate with [webView]
     */
    val context: Context get() = webView.context

    /**
     * Send a default toast message to Android.
     */
    @JavascriptInterface
    fun toast(message: String) {
        context.mainExecutorExt.execute {
            Toast.makeText(context.applicationContext, message, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * In html pages,javascript invoke this bridge method to pass the precise position to Android
     * native layer.
     */
    @JavascriptInterface
    fun transit(left: Float, top: Float, right: Float, bottom: Float)

    companion object {

        operator fun invoke(webView: PowerfulWebView): Android = AndroidImpl(webView)

        const val TAG = "AndroidBridge"
    }
}

internal class AndroidImpl(
    override val webView: WebView
) : Android {

    override val name: String = Android::class.java.simpleName

    @JavascriptInterface
    override fun transit(left: Float, top: Float, right: Float, bottom: Float) {
        if (webView !is PowerfulWebView) {
            Log.w(TAG, UnsupportedOperationException())
            return
        }
        //Not post attention.
        context.mainExecutorExt.execute {
            val r = RectF(left, top, right, bottom)
            webView.domTouchListener?.onDomTouch(webView, webView.hitTestResult, r)
        }
    }
}