package com.genlz.jetpacks.ui.web.bridge

import android.content.Context
import android.graphics.RectF
import android.util.Log
import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.annotation.UiThread
import com.genlz.share.util.appcompat.mainExecutorExt
import com.genlz.share.widget.web.PowerfulWebView
import com.genlz.share.widget.web.bridge.InheritedUiThread
import com.genlz.share.widget.web.bridge.JavascriptBridge


/**
 * All method run in a dedicate thread named "JavaBridge",but you don't
 * care about thread.Just define the method method and implement at here.
 * Annotation is redundant.
 */
class Android(
    override val context: Context,
    private val webView: PowerfulWebView
) : JavascriptBridge {

    @JavascriptInterface
    @InheritedUiThread
    fun toast(message: String) {
        context.mainExecutorExt.execute {
            Toast.makeText(context.applicationContext, message, Toast.LENGTH_SHORT).show()
        }
    }

    @JavascriptInterface
    @InheritedUiThread
    fun transit(left: Float, top: Float, right: Float, bottom: Float) {
        val rectF = RectF(left, top, right, bottom)
        Log.d(TAG, "transit: $rectF")
        context.mainExecutorExt.execute {
            webView.domTouchListener?.invoke(rectF)
        }
    }

    companion object {
        private const val TAG = "Android"
    }
}