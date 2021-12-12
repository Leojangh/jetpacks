package com.genlz.javascript

/**
 * All method run in a dedicated thread named "JavaBridge".So notice the thread context when
 * you implement.
 */
@Suppress("UNUSED")
@Javascript
interface Android {

    /**
     * Send a default toast message to Android.
     */
    fun toast(message: String)

    /**
     * In html pages,javascript invoke this bridge method to pass the precise position to Android
     * native layer.
     *
     * Although the parameter's type is [Double] at here while the bridge
     * method define [Float],javascript numeric type is insensitive.
     */
    fun transit(left: Float, top: Float, right: Float, bottom: Float)
}

