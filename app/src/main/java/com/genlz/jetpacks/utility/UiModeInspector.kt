package com.genlz.jetpacks.utility

import android.app.Application
import android.content.ComponentCallbacks
import android.content.Context
import android.content.res.Configuration
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class UiModeInspector private constructor(
    context: Context,
) {
    init {
        if (context !is Application) throw IllegalArgumentException("$context is not a application context")

        context.registerComponentCallbacks(object : ComponentCallbacks {
            override fun onConfigurationChanged(newConfig: Configuration) {
                getInstance(context).parseNewConfiguration(newConfig)
            }

            override fun onLowMemory() {
                //Don't care.
            }
        })
    }


    private val _darkModeLiveData = MutableLiveData(context.isDarkMode)
    val darkModeLiveData: LiveData<Boolean> = _darkModeLiveData

    private fun parseNewConfiguration(newConfig: Configuration) {
        _darkModeLiveData.value = newConfig.isDarkModeActive
    }

    companion object {

        val Context.isDarkMode
            get() = resources.configuration.isDarkModeActive

        /**
         * Platform R has implemented already.
         */
        val Configuration.isDarkModeActive
            get() = uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        @Volatile
        @JvmStatic
        private var INSTANCE: UiModeInspector? = null

        @JvmStatic
        fun getInstance(context: Context): UiModeInspector {
            if (INSTANCE == null) {
                synchronized(UiModeInspector::class.java) {
                    if (INSTANCE == null)
                        INSTANCE = UiModeInspector(context.applicationContext)
                }
            }
            return INSTANCE!!
        }

        @JvmStatic
        @Deprecated("Use getInstance(context) instead", ReplaceWith("getInstance(context)"))
        fun getInstance(): UiModeInspector {
            return INSTANCE ?: error("Not initialize yet!")
        }

        @Suppress("UNUSED")
        private const val TAG = "UiModeInspector"
    }
}
