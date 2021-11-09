package com.genlz.share.widget.web.bridge

import android.content.Context
import com.genlz.share.util.appcompat.mainExecutorExt
import java.lang.reflect.Proxy

/**
 * A marker interface to indicate being used by javascript bridge.
 */
interface JavascriptBridge {

    val context: Context

    companion object {

        internal const val TAG = "JavascriptBridge"

        /**
         * Wrap the implementation method in appropriate thread,the magic under the
         * hood is powered by dynamic proxy,and determine according to annotation [InheritedUiThread].
         */
        @Suppress("UNCHECKED_CAST")
        fun <T : JavascriptBridge> T.wrap(): T {
            val proxy = Proxy.newProxyInstance(
                context.classLoader,
                arrayOf(*javaClass.interfaces)
            ) { _, method, args ->
                //As maybe NPE corrupt when spread args.
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
            } as T
            return proxy
        }
    }
}

