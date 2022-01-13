package com.genlz.jetpacks.startup

import android.content.Context
import androidx.startup.Initializer
import com.genlz.jetpacks.utility.UiModeInspector

class UiModeInspectorInitializer : Initializer<UiModeInspector> {
    override fun create(context: Context): UiModeInspector {
        return UiModeInspector.getInstance(context)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}