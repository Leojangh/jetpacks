package com.genlz.jetpacks.utility

import android.content.Context
import androidx.startup.Initializer

class UiModeInspectorInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        UiModeInspector.init(context)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}