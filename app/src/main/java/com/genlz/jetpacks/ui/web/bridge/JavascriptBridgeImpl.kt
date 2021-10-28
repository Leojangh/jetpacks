package com.genlz.jetpacks.ui.web.bridge

import android.content.Context
import android.widget.Toast

/**
 * All method run in a dedicate thread named "JavaBridge",but you don't
 * care about thread.Just define the method method and implement at here.
 * Annotation is redundant.
 */
@Suppress("UNUSED")
internal class JavascriptBridgeImpl(
    override val context: Context
) : JavascriptBridge {

    override fun toast(message: String) {
        Toast.makeText(context.applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    override fun transit(left: Float, top: Float, right: Float, bottom: Float) {

    }
}