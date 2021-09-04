package com.genlz.jetpacks.utility

import android.content.Context
import androidx.startup.Initializer

class UiModeInspectorInitializer : Initializer<UiModeInspector> {
    override fun create(context: Context): UiModeInspector {
        return UiModeInspector.getInstance(context)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}