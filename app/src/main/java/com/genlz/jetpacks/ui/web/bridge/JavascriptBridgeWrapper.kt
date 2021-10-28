package com.genlz.jetpacks.ui.web.bridge

/**
 * Static proxy.Add some interceptor at here.
 */
class JavascriptBridgeWrapper(
    private val base: JavascriptBridge
) : JavascriptBridge by base