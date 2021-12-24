package com.genlz.webview.bridge

import android.util.Log
import android.webkit.JavascriptInterface

/**
 * Your gift dears,use it come on.
 */
object Log {

    @JavascriptInterface
    fun v(tag: String?, msg: String) = Log.v(tag, msg)

    @JavascriptInterface
    fun v(tag: String?, msg: String?, t: Throwable?) = Log.v(tag, msg, t)

    @JavascriptInterface
    fun i(tag: String, msg: String) = Log.i(tag, msg)

    @JavascriptInterface
    fun i(tag: String, msg: String?, t: Throwable?) = Log.i(tag, msg, t)

    @JavascriptInterface
    fun d(tag: String, msg: String) = Log.d(tag, msg)

    @JavascriptInterface
    fun d(tag: String, msg: String?, t: Throwable?) = Log.d(tag, msg, t)

    @JavascriptInterface
    fun w(tag: String, msg: String) = Log.w(tag, msg)

    @JavascriptInterface
    fun w(tag: String, msg: String?, t: Throwable?) = Log.w(tag, msg, t)

    @JavascriptInterface
    fun e(tag: String?, msg: String) = Log.e(tag, msg)

    @JavascriptInterface
    fun e(tag: String?, msg: String, t: Throwable?) = Log.e(tag, msg, t)
}