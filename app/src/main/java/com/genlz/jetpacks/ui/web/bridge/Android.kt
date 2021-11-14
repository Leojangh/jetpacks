package com.genlz.jetpacks.ui.web.bridge

import android.content.Context
import android.graphics.RectF
import android.util.Log
import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.annotation.Keep
import com.genlz.share.util.appcompat.mainExecutorExt
import com.genlz.share.widget.web.PowerfulWebView
import com.genlz.share.widget.web.bridge.InheritedUiThread
import com.genlz.share.widget.web.bridge.JavascriptBridge


/**
 * All method run in a dedicate thread named "JavaBridge",but you don't
 * care about thread.Just define the method method and implement at here.
 * Annotation is redundant.
 */
@Suppress("UNUSED")
class Android(
    override val context: Context,
    private val webView: PowerfulWebView
) : JavascriptBridge {

    @Keep
    @JavascriptInterface
    @InheritedUiThread
    fun toast(message: String) {
        context.mainExecutorExt.execute {
            Toast.makeText(context.applicationContext, message, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * In html pages,javascript invoke this bridge method to pass the precise position to Android
     * native layer.
     * [image_listener_hooks.js](#)
     */
    @Keep
    @JavascriptInterface
    @InheritedUiThread
    fun transit(left: Float, top: Float, right: Float, bottom: Float) {
        val rectF = RectF(left, top, right, bottom)
        Log.d(TAG, "transit: $rectF")
        context.mainExecutorExt.execute {
            webView.domTouchListener?.onDomTouch(
                webView,
                webView.hitTestResult,
                rectF
            )
        }
    }

    companion object {
        private const val TAG = "Android"
    }
}