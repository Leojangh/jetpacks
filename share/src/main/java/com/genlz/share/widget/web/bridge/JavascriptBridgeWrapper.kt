package com.genlz.share.widget.web.bridge

/**
 * Static proxy.Add some interceptor at here.
 */
class JavascriptBridgeWrapper(
    private val base: JavascriptBridge
) : JavascriptBridge by base