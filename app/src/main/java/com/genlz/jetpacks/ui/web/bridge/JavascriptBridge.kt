package com.genlz.jetpacks.ui.web.bridge

import android.content.Context
import android.webkit.JavascriptInterface
import com.genlz.jetpacks.utility.appcompat.mainExecutorExt
import java.lang.annotation.Inherited
import java.lang.reflect.Proxy

/**
 * Define the bridge method at here,annotate the method with [InheritedUiThread]
 * which indicates the method needs to be run at main thread if necessary.
 */
interface JavascriptBridge {

    val context: Context

    @InheritedUiThread
    @JavascriptInterface
    fun toast(message: String)

    @JavascriptInterface
    fun transit(left: Float, top: Float, right: Float, bottom: Float)

    companion object {

        private const val TAG = "JavascriptBridge"

        /**
         * Wrap the implementation method in appropriate thread,the magic under the
         * hood is powered by dynamic proxy,and determine according to annotation [InheritedUiThread].
         */
        fun JavascriptBridge.wrap(): JavascriptBridge {
            val proxy = Proxy.newProxyInstance(
                context.classLoader,
                arrayOf(JavascriptBridge::class.java)
            ) { _, method, args ->
                val safeArgs = if (args.isNullOrEmpty()) emptyArray<Any?>() else args
                if (method.declaringClass == Any::class.java) {
                    method(this, *safeArgs)
                } else {
                    if (method.isAnnotationPresent(InheritedUiThread::class.java)) {
                        //Post to ui thread.
                        context.mainExecutorExt.execute {
                            method(this, *safeArgs)
                        }
                    } else {
                        method(this, *safeArgs) //Invoke directly
                    }
                }
            } as JavascriptBridge
            return JavascriptBridgeWrapper(proxy)
        }
    }

    /**
     * A flag annotation to indicate the method needs to be run in UI thread.
     * I must define this instead of [androidx.annotation.UiThread] because that
     * can't be detect in subclass.So [Inherited] to rescue.
     */
    @Retention
    @Inherited
    @Target(AnnotationTarget.FUNCTION)
    private annotation class InheritedUiThread
}

